import React from 'react'
import { Alert, AlertIcon, Container, VStack } from '@chakra-ui/react'
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
  } = useQuery(TASKS_QUERY_KEY, () => getRoomTasks(GLOBAL_ROOM_ID), {
    retry: false
  })

  if (error)
    return (
      <Container maxW="sm" mt={8}>
        <Alert status="error">
          <AlertIcon />
          {error.response.status === 403
            ? 'Please login'
            : `There was an error when creating a new task. ${error.message}`}
        </Alert>
      </Container>
    )

  // noinspection JSValidateTypes
  return (
    !isFetching && (
      <VStack mt={8}>
        {tasks.map(task => (
          <TaskCard key={task.id} task={task} />
        ))}
      </VStack>
    )
  )
}

export default Home
