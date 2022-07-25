import React, { useState } from 'react'
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
  Spinner,
  Text,
  useDisclosure
} from '@chakra-ui/react'
import { GiTwoCoins } from 'react-icons/gi'
import { useMutation, useQuery, useQueryClient } from 'react-query'
import { assignTask, unassignTask } from '../../api/tasks'
import { GLOBAL_ROOM_ID, TASKS_QUERY_KEY } from '../TasksFeed'
import { extractErrorMessage } from '../../api/api-utils'
import { getMember } from '../../api/room'
import { MarkdownContent } from './MarkdownContent'

function TaskAssigmentModal({ isOpen, onClose, taskId, member }) {
  const queryClient = useQueryClient()
  const [error, setError] = useState('')

  const mutation = useMutation(
    /** @param {string} assigneeMemberId */
    assigneeMemberId => assignTask(taskId, assigneeMemberId),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(TASKS_QUERY_KEY).then(() => onClose())
        setError('')
      },
      onError: e => {
        setError(
          e.response.status === 403
            ? "You don't have permissions to assign the task"
            : extractErrorMessage(mutation.error) ||
                `An error occurred when assigning the task`
        )
      }
    }
  )

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Assignee</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <Text>Do you want to assign the task to yourself?</Text>
        </ModalBody>
        <ModalFooter>
          <HStack>
            <Button onClick={onClose}>No</Button>
            <Button onClick={() => mutation.mutate(member.id)}>Yes</Button>
          </HStack>
          {error && (
            <Alert status="error">
              <AlertIcon />
              {error}
            </Alert>
          )}
        </ModalFooter>
      </ModalContent>
    </Modal>
  )
}

function TaskUnassigmentModal({ isOpen, onClose, taskId }) {
  const queryClient = useQueryClient()
  const [error, setError] = useState('')

  const mutation = useMutation(() => unassignTask(taskId), {
    onSuccess: () => {
      queryClient.invalidateQueries(TASKS_QUERY_KEY).then(() => onClose())
      setError('')
    },
    onError: e => {
      setError(
        e.response.status === 403
          ? "You don't have permissions to unassign the task"
          : extractErrorMessage(mutation.error) ||
              `An error occurred when unassigning the task`
      )
    }
  })

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Assignee</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <Text>Do you want to unassign the task?</Text>
        </ModalBody>
        <ModalFooter>
          <HStack>
            <Button onClick={onClose}>No</Button>
            <Button onClick={() => mutation.mutate()}>Yes</Button>
          </HStack>
          {error && (
            <Alert status="error">
              <AlertIcon />
              {error}
            </Alert>
          )}
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
  const {
    isOpen: isOpenAssign,
    onOpen: onOpenAssign,
    isClose: onCloseAssign
  } = useDisclosure()

  const {
    isOpen: isOpenUnassign,
    onOpen: onOpenUnassign,
    isClose: onCloseUnassign
  } = useDisclosure()

  const { data, isFetching, isError } = useQuery(
    ['members'],
    () => getMember(GLOBAL_ROOM_ID),
    {
      retry: false,
      staleTime: 60 * 1000 // 1 min
    }
  )

  const isAssignedToMe = data?.id === task.assigneeMemberId
  const isAssigned = task.assignee !== null

  return (
    <>
      {!isAssigned ? (
        <Button onClick={onOpenAssign}>Unassigned</Button>
      ) : isError ? (
        <Text color="tomato">Error :(</Text>
      ) : isFetching ? (
        <Spinner />
      ) : isAssignedToMe ? (
        <Button colorScheme="pink" onClick={onOpenUnassign}>
          Unassign
        </Button>
      ) : (
        <Text as="b">Assignee: {task.assignee}</Text>
      )}
      <TaskAssigmentModal
        isOpen={isOpenAssign}
        onClose={onCloseAssign}
        taskId={task.id}
        member={data}
      />
      <TaskUnassigmentModal
        isOpen={isOpenUnassign}
        onClose={onCloseUnassign}
        taskId={task.id}
      />
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
  return (
    <Box
      w="100%"
      p={8}
      maxW="3xl"
      borderWidth="1px"
      borderRadius="lg"
      overflow="hidden"
    >
      <Heading as="h3" size="lg" noOfLines={1}>
        {`${task.title}`}
      </Heading>
      <MarkdownContent value={task.content} />
      <Flex justifyContent="space-between" alignItems="center" marginTop={4}>
        <Text>Deadline: {task.deadline}</Text>
        <Text>Author: {task.author}</Text>
        <Assignee task={task} />
        <HStack>
          <Text>Reward: {task.budget}</Text>
          <Icon as={GiTwoCoins} />
        </HStack>
      </Flex>
    </Box>
  )
}

export default TaskCard
