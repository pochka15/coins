import React from 'react'
import { Alert, AlertIcon, VStack } from '@chakra-ui/react'
import TaskCard from './task/TaskCard'
import { useQuery } from 'react-query'
import { getRoomTasks } from '../api/tasks'

const ROOM_ID = 1
export const TASKS_QUERY_KEY = 'tasks'

function Home() {
  const {
    data: tasks,
    isFetching,
    error
  } = useQuery(TASKS_QUERY_KEY, () => getRoomTasks(ROOM_ID))

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
      <VStack>
        {tasks.map(task => (
          <TaskCard key={task.id}>{task.title}</TaskCard>
        ))}
      </VStack>
    )
  )
}

export default Home
