import React from 'react'
import { VStack } from '@chakra-ui/react'
import TaskCard from './task/TaskCard'
import { useQuery } from 'react-query'
import { getRoomTasks } from './api/tasks'

const ROOM_ID = 1
export const TASKS_QUERY_KEY = 'tasks'

function Home() {
  const {
    data: tasks,
    isFetching,
    error
  } = useQuery(TASKS_QUERY_KEY, () => getRoomTasks(ROOM_ID))

  if (isFetching) return 'Fetching...'
  if (error) return `Error: ${error}`
  // noinspection JSValidateTypes
  return (
    <VStack>
      {tasks.map(task => (
        <TaskCard key={task.id}>{task.title}</TaskCard>
      ))}
    </VStack>
  )
}

export default Home
