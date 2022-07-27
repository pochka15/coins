import React from 'react'
import TasksFeed from './TasksFeed'
import { Box, Flex } from '@chakra-ui/react'
import CoinsSummary from './wallet/CoinsSummary'
import RoomsBar from './RoomsBar'

function Home() {
  const columnSize = '2xs'
  return (
    <Flex alignItems="start" justify="center" mt={4}>
      <Box width={columnSize}>
        <RoomsBar />
      </Box>
      <TasksFeed />
      <Box width={columnSize}>
        <CoinsSummary />
      </Box>
    </Flex>
  )
}

export default Home
