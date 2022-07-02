import React from 'react'
import { VStack } from '@chakra-ui/react'
import TaskCard from './task/TaskCard'

/** @type {Task[]} */
const tasks = [
  { id: '1', name: 'Task 1' },
  { id: '2', name: 'Task 2' }
]

function Home() {
  return (
    <VStack>
      {tasks.map(task => (
        <TaskCard key={task.id}>{task.name}</TaskCard>
      ))}
    </VStack>
  )
}

export default Home
