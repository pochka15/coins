import React, { useState } from 'react'
import {
  Alert,
  AlertIcon,
  Link,
  Spinner,
  useColorModeValue,
  VStack
} from '@chakra-ui/react'
import TaskCard from './task/TaskCard'
import { useQuery } from 'react-query'
import { extractErrorMessage } from '../api/api-utils'
import { getRoomTasks } from '../api/room'
import auth from '../security/auth'

export const GLOBAL_ROOM_ID = 'a6041b05-ebb9-4ff0-9b6b-d915d573afb2'
export const TASKS_QUERY_KEY = 'tasks'

function UsosLabel() {
  const gradient = useColorModeValue('linear(to-l, #7928CA, #FF0080)', [
    'linear(to-tr, teal.300, yellow.400)',
    'linear(to-t, blue.200, teal.500)',
    'linear(to-b, orange.100, purple.300)'
  ])
  const [isLinkFocused, setIsLinkFocused] = useState(false)

  return (
    <VStack>
      <Link
        fontSize="4xl"
        fontWeight="extrabold"
        onMouseEnter={() => setIsLinkFocused(true)}
        onMouseLeave={() => setIsLinkFocused(false)}
        onClick={() => auth.startLogin()}
      >
        Login via
      </Link>
      <Link
        bgGradient={gradient}
        bgClip="text"
        fontSize="6xl"
        fontWeight="extrabold"
        onMouseEnter={() => setIsLinkFocused(true)}
        onMouseLeave={() => setIsLinkFocused(false)}
        as={isLinkFocused ? 'u' : 'p'}
        onClick={() => auth.startLogin()}
      >
        USOS
      </Link>
    </VStack>
  )
}

function TasksFeed() {
  const {
    data: tasks,
    isLoading,
    error
  } = useQuery(TASKS_QUERY_KEY, () => getRoomTasks(GLOBAL_ROOM_ID), {
    retry: false,
    refetchOnWindowFocus: true,
    refetchInterval: 60 * 1000, // minute
    keepPreviousData: true
  })

  const shouldLogin = error && error.response.status === 403

  if (isLoading) {
    return (
      <Spinner
        thickness="4px"
        speed="0.65s"
        emptyColor="gray.200"
        color="blue.500"
        size="xl"
      />
    )
  }

  if (shouldLogin) return <UsosLabel />

  if (error)
    return (
      <Alert status="error" maxW="2xl">
        <AlertIcon />
        {extractErrorMessage(error) || `There was an error when fetching tasks`}
      </Alert>
    )

  // noinspection JSValidateTypes
  return (
    <VStack width="2xl">
      {tasks.map(task => (
        <TaskCard key={task.id} task={task} />
      ))}
    </VStack>
  )
}

export default TasksFeed
