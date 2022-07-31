import React from 'react'
import { Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { useQuery } from 'react-query'
import { getWallet } from '../../api/wallets'
import { useCurrentRoom } from '../../hooks/use-current-room'

export const WALLET_KEY = 'wallet'

function CoinsAmount({ children }) {
  const gradient = useColorModeValue('linear(to-l, #7928CA, #FF0080)', [
    'linear(to-tr, teal.300, yellow.400)',
    'linear(to-t, blue.200, teal.500)',
    'linear(to-b, orange.100, purple.300)'
  ])

  return (
    <Text
      bgGradient={gradient}
      bgClip="text"
      fontSize="6xl"
      fontWeight="extrabold"
      height={20}
    >
      {children}
    </Text>
  )
}

function CoinsSummary() {
  const room = useCurrentRoom()

  const {
    data: wallet,
    isLoading,
    isError
  } = useQuery([WALLET_KEY, room?.id], () => getWallet(room.id), {
    retry: false,
    refetchOnWindowFocus: true,
    keepPreviousData: true
  })

  if (!room) return null

  return (
    !isLoading &&
    !isError && (
      <VStack>
        <CoinsAmount>{wallet.coinsAmount}</CoinsAmount>
        <Text fontSize="4xl">Coins</Text>
      </VStack>
    )
  )
}

export default CoinsSummary
