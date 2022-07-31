import React, { useEffect, useState } from 'react'
import TasksFeed from './TasksFeed'
import {
  Alert,
  AlertIcon,
  Box,
  CloseButton,
  Flex,
  HStack,
  Link,
  Spinner,
  Text,
  useBoolean,
  useColorModeValue,
  VStack
} from '@chakra-ui/react'
import CoinsSummary from './wallet/CoinsSummary'
import RoomsBar from './RoomsBar'
import auth from '../security/auth'
import { useCurrentRoom } from '../hooks/use-current-room'
import { useDebounce } from '../hooks/use-debounce-component-value'

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

/**
 *
 * @param {TNotification} notification
 * @constructor
 */
function Notification({ notification }) {
  const { type } = notification
  const { message } = notification.payload
  const dMessage = useDebounce(message, 3000)
  const [hidden, setHidden] = useBoolean()
  useEffect(() => setHidden.off(), [notification, setHidden])

  if ((dMessage === message && type !== 'error') || hidden) return null

  if (type === 'loading') {
    return (
      <Alert mt="8" status="info" borderRadius="md">
        <Spinner
          thickness="4px"
          speed="0.65s"
          emptyColor="gray.200"
          color="blue.500"
          mr={2}
        />
        {message}
      </Alert>
    )
  }

  if (type === 'error') {
    return (
      <Alert mt="8" status={type} borderRadius="md">
        {message}
        <CloseButton onClick={setHidden.on} />
      </Alert>
    )
  }

  return (
    <Alert mt="8" status={type} borderRadius="md">
      <AlertIcon />
      {message}
    </Alert>
  )
}

/**
 * Home component
 * @param {TNotification | null} notification
 * @return {JSX.Element}
 * @constructor
 */
function Home({ notification }) {
  const columnSize = '2xs'
  const room = useCurrentRoom()

  return (
    <Flex alignItems="start" justify="center" mt={4} gap={4} flexWrap="wrap">
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
            {notification && <Notification notification={notification} />}
          </Box>
        </>
      )}
    </Flex>
  )
}

export default Home
