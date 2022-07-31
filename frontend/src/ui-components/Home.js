import React, { useState } from 'react'
import TasksFeed from './TasksFeed'
import {
  Box,
  Flex,
  HStack,
  Link,
  Text,
  useColorModeValue,
  VStack
} from '@chakra-ui/react'
import CoinsSummary from './wallet/CoinsSummary'
import RoomsBar from './RoomsBar'
import auth from '../security/auth'
import { useCurrentRoom } from '../hooks/use-current-room'

export function UsosLabel(props) {
  const gradient = useColorModeValue('linear(to-l, #7928CA, #FF0080)', [
    'linear(to-tr, teal.300, yellow.400)',
    'linear(to-t, blue.200, teal.500)',
    'linear(to-b, orange.100, purple.300)'
  ])
  const [isLinkFocused, setIsLinkFocused] = useState(false)

  return (
    <VStack {...props}>
      <Link
        fontSize="4xl"
        fontWeight="extrabold"
        onMouseEnter={() => setIsLinkFocused(true)}
        onMouseLeave={() => setIsLinkFocused(false)}
        onClick={() => auth.startLogin()}
      >
        Login via
      </Link>
      <Link
        bgGradient={gradient}
        bgClip="text"
        fontSize="6xl"
        fontWeight="extrabold"
        onMouseEnter={() => setIsLinkFocused(true)}
        onMouseLeave={() => setIsLinkFocused(false)}
        as={isLinkFocused ? 'u' : 'p'}
        onClick={() => auth.startLogin()}
      >
        USOS
      </Link>
    </VStack>
  )
}

function ChooseRoomLabel() {
  const gradient = useColorModeValue('linear(to-l, #7928CA, #FF0080)', [
    'linear(to-tr, teal.300, yellow.400)',
    'linear(to-t, blue.200, teal.500)',
    'linear(to-b, orange.100, purple.300)'
  ])

  return (
    <HStack>
      <Text
        bgGradient={gradient}
        bgClip="text"
        fontSize="4xl"
        fontWeight="extrabold"
      >
        Choose room
      </Text>
    </HStack>
  )
}

function Home() {
  const columnSize = '2xs'
  const room = useCurrentRoom()

  return (
    <Flex alignItems="start" justify="center" mt={4}>
      {!auth.isLogged() && <UsosLabel />}

      {auth.isLogged() && room === null && (
        <>
          <Box width={columnSize}>
            <ChooseRoomLabel />
            <RoomsBar />
          </Box>
        </>
      )}

      {auth.isLogged() && room !== null && (
        <>
          <Box width={columnSize}>
            <RoomsBar />
          </Box>

          <TasksFeed />

          <Box width={columnSize}>
            <CoinsSummary />
          </Box>
        </>
      )}
    </Flex>
  )
}

export default Home
