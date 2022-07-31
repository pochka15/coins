import React from 'react'
import { Alert, AlertIcon, Spinner, VStack } from '@chakra-ui/react'
import TaskCard from './task/TaskCard'
import { useQuery } from 'react-query'
import { extractErrorMessage } from '../api/api-utils'
import { getRoomTasks } from '../api/room'
import auth from '../security/auth'
import { useCurrentRoom } from '../hooks/use-current-room'

export const TASKS_QUERY_KEY = 'tasks'

function TasksFeed() {
  const room = useCurrentRoom()
  const {
    data: tasks,
    isLoading,
    error
  } = useQuery([TASKS_QUERY_KEY, room?.id], () => getRoomTasks(room.id), {
    retry: false,
    refetchOnWindowFocus: true,
    refetchInterval: 60 * 1000, // minute
    keepPreviousData: true
  })

  const shouldLogout = error && error.response.status === 403
  if (shouldLogout) return auth.logout()

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

  if (error)
    return (
      <Alert status="error" maxW="2xl">
        <AlertIcon />
        {extractErrorMessage(error) || `There was an error when fetching tasks`}
      </Alert>
    )

  // noinspection JSValidateTypes
  return (
    <VStack>
      {tasks.map(task => (
        <TaskCard key={task.id} task={task} />
      ))}
    </VStack>
  )
}

export default TasksFeed
