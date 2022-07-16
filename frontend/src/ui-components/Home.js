import React from 'react'
import { Alert, AlertIcon, VStack } from '@chakra-ui/react'
import TaskCard from './task/TaskCard'
import { useQuery } from 'react-query'
import { getRoomTasks } from '../api/tasks'

export const GLOBAL_ROOM_ID = 'a6041b05-ebb9-4ff0-9b6b-d915d573afb2'
export const TASKS_QUERY_KEY = 'tasks'

function Home() {
  const {
    data: tasks,
    isFetching,
    error
  } = useQuery(TASKS_QUERY_KEY, () => getRoomTasks(GLOBAL_ROOM_ID))

  if (error)
    return (
      <Alert status="error">
        <AlertIcon />
        There was an error when creating a new task: {error.message}
      </Alert>
    )

  // noinspection JSValidateTypes
  return (
    !isFetching && (
      <VStack marginTop={8}>
        {tasks.map(task => (
          <TaskCard key={task.id} task={task} />
        ))}
      </VStack>
    )
  )
}

export default Home
