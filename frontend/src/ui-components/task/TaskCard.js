import React, { useEffect, useState } from 'react'
import {
  Alert,
  AlertIcon,
  Box,
  Button,
  Flex,
  Heading,
  HStack,
  Icon,
  IconButton,
  Input,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Popover,
  PopoverArrow,
  PopoverBody,
  PopoverCloseButton,
  PopoverContent,
  PopoverTrigger,
  Show,
  Spinner,
  Text,
  Tooltip,
  useBoolean,
  useColorModeValue,
  useDisclosure
} from '@chakra-ui/react'
import { GiTwoCoins } from 'react-icons/gi'
import { useMutation, useQuery, useQueryClient } from 'react-query'
import {
  acceptTask,
  assignTask,
  deleteTask,
  rejectTask,
  solveTask,
  unassignTask
} from '../../api/tasks'
import { TASKS_QUERY_KEY } from '../TasksFeed'
import {
  extractErrorMessage,
  extractValidationErrors,
  toErrorMessage
} from '../../api/api-utils'
import { getMember } from '../../api/room'
import { MarkdownContent } from './MarkdownContent'
import { RiDeleteBin7Line } from 'react-icons/ri'
import { MdDone } from 'react-icons/md'
import { WALLET_KEY } from '../wallet/CoinsSummary'
import { useCurrentRoom } from '../../hooks/use-current-room'

function formatSolutionNote(note) {
  return note ? note : 'empty'
}

function buildErrorMessage(
  error,
  noPermissionsMessage = `You don't have permissions`,
  defaultMessage = 'An error occurred'
) {
  if (error.response.status === 403) return noPermissionsMessage
  return extractErrorMessage(error) || defaultMessage
}

/**
 * Get all the utility info about member
 * @param {ApiTask} task
 * @param {ApiMember} member
 * @return {TMemberInformation}
 */
function getInfoAboutMember(task, member) {
  return {
    isAuthor: task.authorMemberId === member?.id,
    isAssignee: task.assigneeMemberId === member?.id
  }
}

/**
 *
 * @param {ApiTask} task
 * @param {TMemberInformation} memberInfo
 * @return {TTaskPermissions}
 */
function getTaskPermissions(task, memberInfo) {
  const canAccept = memberInfo.isAuthor && task.status === 'Reviewing'
  return {
    canSolve: memberInfo.isAssignee && task.status === 'Assigned',
    canAccept,
    canReject: canAccept
  }
}

function useMember() {
  const room = useCurrentRoom()
  const { data, isFetching, isError } = useQuery(
    ['members', room.id],
    () => getMember(room.id),
    {
      retry: false,
      staleTime: 60 * 1000 // 1 min
    }
  )
  return { data, isFetching, isError }
}

function PopoverError({ errorMessage, children, onClose: propOnClose }) {
  const { isOpen, onOpen, onClose } = useDisclosure()
  useEffect(() => {
    if (errorMessage) onOpen()
  }, [errorMessage, onOpen])

  return (
    <Popover
      returnFocusOnClose={false}
      closeOnBlur={false}
      isOpen={isOpen}
      onClose={() => {
        onClose()
        propOnClose()
      }}
    >
      <PopoverTrigger>{children}</PopoverTrigger>
      <PopoverContent>
        <PopoverArrow />
        <PopoverCloseButton />
        <PopoverBody>
          <Alert status="error" mt={8}>
            <AlertIcon />
            {errorMessage}
          </Alert>
        </PopoverBody>
      </PopoverContent>
    </Popover>
  )
}

function AssignmentButton({ member, task }) {
  const queryClient = useQueryClient()
  const [errorMessage, setErrorMessage] = useState('')

  const mutation = useMutation(() => assignTask(task.id, member.id), {
    onSuccess: () => {
      queryClient.invalidateQueries(TASKS_QUERY_KEY).then()
      setErrorMessage('')
    },
    onError: e => {
      setErrorMessage(
        buildErrorMessage(
          e,
          `You don't have permissions to assign the task`,
          'An error occurred when assigning the task'
        )
      )
    }
  })

  return (
    <PopoverError
      errorMessage={errorMessage}
      onClose={() => setErrorMessage('')}
    >
      <Button onClick={mutation.mutate} variant="ghost">
        <Text as="u">Assign to me</Text>
      </Button>
    </PopoverError>
  )
}

function ClearAssignmentButton({ task }) {
  const queryClient = useQueryClient()
  const [errorMessage, setErrorMessage] = useState('')

  const mutation = useMutation(() => unassignTask(task.id), {
    onSuccess: () => {
      queryClient.invalidateQueries(TASKS_QUERY_KEY).then()
      setErrorMessage('')
    },
    onError: e => {
      setErrorMessage(
        buildErrorMessage(
          e,
          `You don't have permissions to unassign the task`,
          'An error occurred when clearing assignee'
        )
      )
    }
  })

  return (
    <PopoverError
      errorMessage={errorMessage}
      onClose={() => setErrorMessage('')}
    >
      <Button onClick={mutation.mutate} variant="ghost">
        <Text as="u">Unassign</Text>
      </Button>
    </PopoverError>
  )
}

/**
 *
 * @param isOpen
 * @param onClose
 * @param {ApiTask} task
 * @return {JSX.Element}
 * @constructor
 */
function TaskEditor({ isOpen, onClose, task }) {
  const queryClient = useQueryClient()
  const [error, setError] = useState('')

  const prefix = 'Do you want to delete this task?'
  const suffix =
    task.status === 'Closed'
      ? ''
      : ` You will receive your ${task.budget} coins back`
  const text = prefix + suffix

  const mutation = useMutation(() => deleteTask(task.id), {
    onSuccess: () => {
      queryClient
        .invalidateQueries(TASKS_QUERY_KEY)
        .then(() => queryClient.invalidateQueries(WALLET_KEY))
        .then(() => onClose())
      setError('')
    },
    onError: e => {
      setError(
        buildErrorMessage(
          e,
          `You don't have permissions to delete task`,
          'An error occurred when deleting a task'
        )
      )
    }
  })

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>{task.title}</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <Text>{text}</Text>
          {error && (
            <Alert status="error" mt={8}>
              <AlertIcon />
              {error}
            </Alert>
          )}
        </ModalBody>
        <ModalFooter>
          <HStack>
            <Button onClick={onClose}>No</Button>
            <Button
              onClick={mutation.mutate}
              variant="outline"
              colorScheme="red"
            >
              Yes
            </Button>
          </HStack>
        </ModalFooter>
      </ModalContent>
    </Modal>
  )
}

/**
 *
 * @param {ApiTask} task
 * @return {JSX.Element}
 * @constructor
 */
function Assignee({ task }) {
  const { data: member, isFetching, isError } = useMember()
  const isAssignedToMe = member?.id === task.assigneeMemberId
  const isAssigned = task.assignee !== null

  return (
    <>
      {!isAssigned ? (
        <AssignmentButton member={member} task={task} />
      ) : isError ? (
        <Text color="red.500">Error :(</Text>
      ) : isFetching ? (
        <Spinner />
      ) : isAssignedToMe ? (
        <ClearAssignmentButton task={task} />
      ) : (
        <Text as="b">Assignee: {task.assignee}</Text>
      )}
    </>
  )
}

function SolveTaskButton({ onClick }) {
  return (
    <Tooltip label="Solve task">
      <IconButton
        aria-label="Solve task"
        icon={<MdDone />}
        onClick={onClick}
        variant="ghost"
      />
    </Tooltip>
  )
}

function AcceptTaskButton({ task }) {
  const queryClient = useQueryClient()
  const [errorMessage, setErrorMessage] = useState('')

  const mutation = useMutation(() => acceptTask(task.id), {
    onSuccess: () => {
      queryClient
        .invalidateQueries(TASKS_QUERY_KEY)
        .then(() => queryClient.invalidateQueries(WALLET_KEY))

      setErrorMessage('')
    },
    onError: e => {
      setErrorMessage(
        buildErrorMessage(
          e,
          `You don't have permissions to accept this task`,
          'An error occurred when accepting the task'
        )
      )
    }
  })

  if (errorMessage) {
    return (
      <Alert status="error" marginLeft={8}>
        <AlertIcon />
        {errorMessage}
      </Alert>
    )
  }

  return (
    <Tooltip label="Accept task">
      <Button onClick={mutation.mutate} aria-label="Accept task">
        Accept
      </Button>
    </Tooltip>
  )
}

function RejectTaskButton({ task }) {
  const queryClient = useQueryClient()
  const [errorMessage, setErrorMessage] = useState('')

  const mutation = useMutation(() => rejectTask(task.id), {
    onSuccess: () => {
      queryClient.invalidateQueries(TASKS_QUERY_KEY).then()
      setErrorMessage('')
    },
    onError: e => {
      setErrorMessage(
        buildErrorMessage(
          e,
          `You don't have permissions to reject this task`,
          'An error occurred when rejecting the task'
        )
      )
    }
  })

  if (errorMessage) {
    return (
      <Alert status="error" marginLeft={8}>
        <AlertIcon />
        {errorMessage}
      </Alert>
    )
  }

  return (
    <Tooltip label="Reject task">
      <Button onClick={mutation.mutate} aria-label="Reject task">
        Reject
      </Button>
    </Tooltip>
  )
}

function TaskModifiersPanel({ task, disabled, onSolveTaskClick }) {
  const { data: member } = useMember()
  const { isOpen, onOpen, onClose } = useDisclosure()
  const me = getInfoAboutMember(task, member)
  const permissions = getTaskPermissions(task, me)

  if (disabled) return null

  return (
    <Flex justifyContent="start" h={4}>
      {permissions.canSolve && <SolveTaskButton onClick={onSolveTaskClick} />}
      {permissions.canAccept && <AcceptTaskButton task={task} />}
      {permissions.canReject && <RejectTaskButton task={task} />}

      {me.isAuthor && (
        <IconButton
          onClick={onOpen}
          icon={<RiDeleteBin7Line />}
          aria-label="Edit task"
          variant="ghost"
        />
      )}
      <TaskEditor isOpen={isOpen} onClose={onClose} task={task} />
    </Flex>
  )
}

function SolveTaskModal({ isOpen, onClose, task }) {
  const queryClient = useQueryClient()
  const [errorMessage, setErrorMessage] = useState('')
  const [solutionNote, setSolutionNote] = useState('')

  const mutation = useMutation(() => solveTask(task.id, solutionNote), {
    onSuccess: () => {
      queryClient.invalidateQueries(TASKS_QUERY_KEY).then()
      setErrorMessage('')
      onClose()
    },
    onError: e => {
      const validationErrors = extractValidationErrors(e) || []
      const message =
        toErrorMessage(validationErrors) ||
        extractErrorMessage(e) ||
        `An error occurred when solving the task`
      setErrorMessage(message)
    }
  })

  const handleSolveTask = () => {
    mutation.mutate()
    onClose()
  }

  return (
    <>
      {errorMessage && (
        <Alert status="error" marginLeft={8}>
          <AlertIcon />
          {errorMessage}
        </Alert>
      )}

      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Leave a note</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Input
              id="title"
              type="text"
              onChange={e => setSolutionNote(e.target.value)}
              value={solutionNote}
            />

            <Button mt={4} onClick={handleSolveTask}>
              Solve task
            </Button>
          </ModalBody>
          <ModalFooter />
        </ModalContent>
      </Modal>
    </>
  )
}

/**
 * Card representing task
 * @param {ApiTask} task
 * @return {JSX.Element}
 * @constructor
 */
function TaskCard({ task }) {
  const [isHovering, setIsHovering] = useBoolean()
  const [isSolveTaskModalOpen, setIsSolveTaskModalOpen] = useBoolean()
  const hoverColor = useColorModeValue('blackAlpha.50', 'whiteAlpha.100')
  const color = isHovering ? hoverColor : ''

  return (
    <Box
      p={8}
      w={[72, 'md', '3xl']}
      borderWidth="1px"
      borderRadius="lg"
      overflow="hidden"
      bgColor={color}
      onMouseEnter={setIsHovering.on}
      onMouseLeave={setIsHovering.off}
    >
      <Flex justifyContent="space-between">
        <Heading as="h3" size="lg" noOfLines={1} maxWidth={'lg'}>
          {`${task.title}`}
        </Heading>
        <TaskModifiersPanel
          task={task}
          disabled={!isHovering}
          onSolveTaskClick={setIsSolveTaskModalOpen.on}
        />
      </Flex>

      <MarkdownContent value={task.content} mt={2} />

      {task.status === 'Reviewing' && task.solutionNote && (
        <MarkdownContent
          value={`Note: ${formatSolutionNote(task.solutionNote)}`}
          mt={2}
        />
      )}

      <Flex marginTop={6} gap={4} align="center">
        <Show above="sm">
          <Text w={44}>
            {task.deadline ? `Deadline: ${task.deadline}` : ''}
          </Text>
        </Show>

        <Show above="sm">
          <Text w="3xs">Author: {task.author}</Text>
        </Show>

        {task.status === 'Reviewing' ? (
          <Text>Reviewing</Text>
        ) : task.status === 'Closed' ? (
          <Text>Closed ({task.assignee})</Text>
        ) : (
          <Assignee task={task} />
        )}
        <HStack>
          <Text>Reward: {task.budget}</Text>
          <Icon as={GiTwoCoins} />
        </HStack>
      </Flex>

      <SolveTaskModal
        isOpen={isSolveTaskModalOpen}
        onClose={setIsSolveTaskModalOpen.off}
        task={task}
      />
    </Box>
  )
}

export default TaskCard
