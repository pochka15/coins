import React from 'react'
import TasksFeed from './TasksFeed'
import { Box, Flex } from '@chakra-ui/react'
import CoinsSummary from './wallet/CoinsSummary'

function Home() {
  const columnSize = '2xs'
  return (
    <Flex alignItems="start" justify="center" mt={4}>
      <Box width={columnSize} />
      <TasksFeed />
      <Box width={columnSize}>
        <CoinsSummary />
      </Box>
    </Flex>
  )
}

export default Home
