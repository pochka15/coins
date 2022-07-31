import React from 'react'
import usePersistentContext from '../hooks/use-persistent-context'
import { useQuery } from 'react-query'
import { getAvailableRooms } from '../api/user'
import { Button, Spinner, Text, VStack } from '@chakra-ui/react'
import { CURRENT_ROOM_KEY, useCurrentRoom } from '../hooks/use-current-room'

function RoomsBar() {
  const [_, setRawRoom] = usePersistentContext(CURRENT_ROOM_KEY)
  const room = useCurrentRoom()
  const setRoom = room => setRawRoom(JSON.stringify(room))

  const {
    data: rooms,
    isLoading,
    isError,
    error
  } = useQuery(['availableRooms'], getAvailableRooms, {
    refetchOnWindowFocus: true,
    retry: false,
    keepPreviousData: true
  })

  if (isError) {
    return error.response.status === 403 ? null : (
      <Text color="tomato">
        Some error occurred when fetching available rooms
      </Text>
    )
  }

  if (isLoading) {
    return (
      <Spinner
        thickness="4px"
        speed="0.65s"
        emptyColor="gray.200"
        color="blue.500"
      />
    )
  }

  return (
    <VStack borderRight="1px" w={32}>
      {rooms.map(
        /** @param {ApiRoom} x */
        x => {
          return (
            <Button
              key={x.id}
              variant={x.id === room?.id ? 'outline' : 'ghost'}
              onClick={() => setRoom(x)}
              w="100%"
              justifyContent="left"
            >
              <Text noOfLines={1}>{x.name}</Text>
            </Button>
          )
        }
      )}
    </VStack>
  )
}

export default RoomsBar
