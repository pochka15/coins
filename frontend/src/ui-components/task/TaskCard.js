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
  Text,
  useDisclosure
} from '@chakra-ui/react'
import { GiTwoCoins } from 'react-icons/gi'
import { useMutation, useQuery, useQueryClient } from 'react-query'
import { assignTask } from '../../api/tasks'
import { GLOBAL_ROOM_ID, TASKS_QUERY_KEY } from '../TasksFeed'
import { extractErrorMessage } from '../../api/api-utils'
import { getMember } from '../../api/room'
import { MarkdownContent } from './MarkdownContent'

function TaskAssigmentModal({ isOpen, onClose, taskId }) {
  const queryClient = useQueryClient()
  const [error, setError] = useState('')

  const {
    data: member,
    isFetching,
    isError: isMemberFetchError
  } = useQuery(['members'], () => getMember(GLOBAL_ROOM_ID), {
    retry: false,
    staleTime: 5 * 60 * 1000 // 5 min
  })

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
            ? "You don't have permissions to create a task"
            : extractErrorMessage(mutation.error) ||
                `An error occurred when assigning a task`
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
            <Button
              onClick={() => mutation.mutate(member.id)}
              disabled={isFetching || isMemberFetchError}
            >
              Yes
            </Button>
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
 * Card representing task
 * @param {ApiTask} task
 * @return {JSX.Element}
 * @constructor
 */
function TaskCard({ task }) {
  const { isOpen, onOpen, onClose } = useDisclosure()

  return (
    <>
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
          {task.assignee ? (
            <Text as="b">Assignee: {task.assignee}</Text>
          ) : (
            <Button onClick={onOpen}>Unassigned</Button>
          )}
          <HStack>
            <Text>Reward: {task.budget}</Text>
            <Icon as={GiTwoCoins} />
          </HStack>
        </Flex>
      </Box>
      <TaskAssigmentModal isOpen={isOpen} onClose={onClose} taskId={task.id} />
    </>
  )
}

export default TaskCard
