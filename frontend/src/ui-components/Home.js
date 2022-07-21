import React from 'react'
import TasksFeed from './TasksFeed'
import { Box, Container, Flex } from '@chakra-ui/react'
import CoinsSummary from './wallet/CoinsSummary'

function Home() {
  return (
    <Container maxWidth="container.xl" mt={4}>
      <Flex gap={2}>
        {/* First column */}
        <Box width="2xs" />

        {/* Second column */}
        <TasksFeed />

        {/* Third column */}
        <CoinsSummary />
      </Flex>
    </Container>
  )
}

export default Home
