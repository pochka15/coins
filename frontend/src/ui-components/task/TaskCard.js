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
  Spinner,
  Text,
  useColorModeValue,
  useDisclosure
} from '@chakra-ui/react'
import { GiTwoCoins } from 'react-icons/gi'
import { useMutation, useQuery, useQueryClient } from 'react-query'
import { assignTask, deleteTask, unassignTask } from '../../api/tasks'
import { GLOBAL_ROOM_ID, TASKS_QUERY_KEY } from '../TasksFeed'
import { extractErrorMessage } from '../../api/api-utils'
import { getMember } from '../../api/room'
import { MarkdownContent } from './MarkdownContent'
import { RiDeleteBin7Line } from 'react-icons/ri'
import { WALLET_KEY } from '../wallet/CoinsSummary'

function buildErrorMessage(
  error,
  noPermissionsMessage = `You don't have permissions`,
  defaultMessage = 'An error occurred'
) {
  if (error.response.status === 403) return noPermissionsMessage
  return extractErrorMessage(error) || defaultMessage
}

function useMember() {
  const { data, isFetching, isError } = useQuery(
    ['members'],
    () => getMember(GLOBAL_ROOM_ID),
    {
      retry: false,
      staleTime: 60 * 1000 // 1 min
    }
  )
  return { data, isFetching, isError }
}

function PopoverError({
  errorMessage,
  onHoverChange,
  children,
  onClose: propOnClose
}) {
  const { isOpen, onOpen, onClose } = useDisclosure()
  useEffect(() => {
    if (errorMessage) onOpen()
  }, [errorMessage])

  return (
    <Popover
      returnFocusOnClose={false}
      isOpen={isOpen}
      onClose={() => {
        onClose()
        propOnClose()
      }}
      closeOnBlur={false}
    >
      <PopoverTrigger>{children}</PopoverTrigger>
      <PopoverContent>
        <PopoverArrow />
        <PopoverCloseButton
          onMouseEnter={() => onHoverChange(true)}
          onMouseLeave={() => onHoverChange(false)}
        />
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

function AssignmentButton({ onHoverChange, member, taskId }) {
  const queryClient = useQueryClient()
  const [errorMessage, setErrorMessage] = useState('')

  const mutation = useMutation(() => assignTask(taskId, member.id), {
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
      onHoverChange={onHoverChange}
      onClose={() => setErrorMessage('')}
    >
      <Button
        onMouseEnter={() => onHoverChange(true)}
        onMouseLeave={() => onHoverChange(false)}
        onClick={() => mutation.mutate()}
        variant="ghost"
      >
        <Text as="u">Assign to me</Text>
      </Button>
    </PopoverError>
  )
}

function ClearAssignmentButton({ onHoverChange, taskId }) {
  const queryClient = useQueryClient()
  const [errorMessage, setErrorMessage] = useState('')

  const mutation = useMutation(() => unassignTask(taskId), {
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
      onHoverChange={onHoverChange}
      onClose={() => setErrorMessage('')}
    >
      <Button
        onMouseEnter={() => onHoverChange(true)}
        onMouseLeave={() => onHoverChange(false)}
        onClick={() => mutation.mutate()}
        variant="ghost"
      >
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

  const mutation = useMutation(
    /** @param {string} taskId */
    taskId => deleteTask(taskId),
    {
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
    }
  )

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>{task.title}</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <Text>
            Do you want to delete this task? You will receive {task.budget}{' '}
            coins back
          </Text>
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
              rightIcon={<RiDeleteBin7Line />}
              onClick={() => mutation.mutate(task.id)}
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
 * @param {function(boolean): void} onHoverChange
 * @param {ApiMember} member
 * @return {JSX.Element}
 * @constructor
 */
function Assignee({ task, onHoverChange, member }) {
  const { data, isFetching, isError } = useMember()
  const isAssignedToMe = data?.id === task.assigneeMemberId
  const isAssigned = task.assignee !== null

  return (
    <>
      {!isAssigned ? (
        <AssignmentButton
          onHoverChange={onHoverChange}
          member={member}
          taskId={task.id}
        />
      ) : isError ? (
        <Text color="red.500">Error :(</Text>
      ) : isFetching ? (
        <Spinner />
      ) : isAssignedToMe ? (
        <ClearAssignmentButton onHoverChange={onHoverChange} taskId={task.id} />
      ) : (
        <Text as="b">Assignee: {task.assignee}</Text>
      )}
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
  const { isOpen: isTaskEditorOpen, onOpen, onClose } = useDisclosure()
  const { data: member } = useMember()
  const [isHoveringCard, setIsHoveringCard] = useState(false)
  const [isHoveringAssignee, setIsHoveringAssignee] = useState(false)
  const isMine = task.authorMemberId === member?.id
  const color = useColorModeValue('blackAlpha.300', 'whiteAlpha.100')

  return (
    <>
      <Box
        p={8}
        w={['1xl', '2xl', '3xl']}
        borderWidth="1px"
        borderRadius="lg"
        overflow="hidden"
        bgColor={isHoveringCard && isMine && color}
        onMouseEnter={() => setIsHoveringCard(true)}
        onMouseLeave={() => setIsHoveringCard(false)}
        cursor={isHoveringCard && isMine && 'pointer'}
        onClick={() => !isHoveringAssignee && isMine && onOpen()}
      >
        <Heading as="h3" size="lg" noOfLines={1}>
          {`${task.title}`}
        </Heading>
        <MarkdownContent value={task.content} />
        <Flex marginTop={4} gap={4} align="center">
          <Text w="11rem">Deadline: {task.deadline}</Text>
          <Text w="3xs">Author: {task.author}</Text>
          <Assignee
            task={task}
            onHoverChange={setIsHoveringAssignee}
            member={member}
          />
          <HStack>
            <Text>Reward: {task.budget}</Text>
            <Icon as={GiTwoCoins} />
          </HStack>
        </Flex>
      </Box>
      <TaskEditor isOpen={isTaskEditorOpen} onClose={onClose} task={task} />
    </>
  )
}

export default TaskCard
