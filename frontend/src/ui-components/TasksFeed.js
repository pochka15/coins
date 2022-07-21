import React from 'react'
import { Alert, AlertIcon, VStack } from '@chakra-ui/react'
import TaskCard from './task/TaskCard'
import { useQuery } from 'react-query'
import { getRoomTasks } from '../api/tasks'
import { extractErrorMessage } from '../api/api-utils'

export const GLOBAL_ROOM_ID = 'a6041b05-ebb9-4ff0-9b6b-d915d573afb2'
export const TASKS_QUERY_KEY = 'tasks'

function TasksFeed() {
  const {
    data: tasks,
    isFetching,
    error
  } = useQuery(TASKS_QUERY_KEY, () => getRoomTasks(GLOBAL_ROOM_ID), {
    retry: false,
    refetchOnWindowFocus: true
  })

  if (error)
    return (
      <Alert status="error" maxW="2xl">
        <AlertIcon />
        {error.response.status === 403
          ? 'Please login'
          : extractErrorMessage(error) ||
            `There was an error when fetching tasks`}
      </Alert>
    )

  // noinspection JSValidateTypes
  return (
    !isFetching && (
      <VStack width="2xl">
        {tasks.map(task => (
          <TaskCard key={task.id} task={task} />
        ))}
      </VStack>
    )
  )
}

export default TasksFeed
