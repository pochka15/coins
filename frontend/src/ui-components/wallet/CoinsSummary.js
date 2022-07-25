import React from 'react'
import { Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { useQuery } from 'react-query'
import { getWallet } from '../../api/wallets'
import { GLOBAL_ROOM_ID } from '../TasksFeed'

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
  const {
    data: wallet,
    isFetching,
    isError
  } = useQuery([WALLET_KEY], () => getWallet(GLOBAL_ROOM_ID), {
    retry: false,
    refetchOnWindowFocus: true
  })

  return (
    !isFetching &&
    !isError && (
      <VStack width="2xs">
        <CoinsAmount>{wallet.coinsAmount}</CoinsAmount>
        <Text fontSize="4xl">Coins</Text>
      </VStack>
    )
  )
}

export default CoinsSummary
