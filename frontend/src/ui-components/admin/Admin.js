import React, { useState } from 'react'
import { useQuery } from 'react-query'
import { getMe } from '../../api/user'
import auth from '../../security/auth'
import { Navigate, useNavigate } from 'react-router-dom'
import {
  Alert,
  AlertIcon,
  Center,
  HStack,
  IconButton,
  Spinner,
  Text
} from '@chakra-ui/react'
import { extractErrorMessage } from '../../api/api-utils'
import { AiOutlineHome } from 'react-icons/ai'
import NewRoom from './NewRoom'

function Admin() {
  const [errorMessage, setErrorMessage] = useState('')
  const navigate = useNavigate()

  const {
    /** @type {ApiUser} */
    data: me,
    isFetching
  } = useQuery(['me'], getMe, {
    retry: false,
    enabled: auth.isLogged(),
    staleTime: 5 * 60 * 1000, // 5 min
    onError: e =>
      setErrorMessage(
        extractErrorMessage(e) ||
          ':( Unknown occurred when fetching my user, try logging again'
      )
  })

  if (!auth.isLogged) return <Navigate to="/" />

  if (isFetching) {
    return (
      <Spinner
        thickness="4px"
        speed="0.65s"
        emptyColor="gray.200"
        color="blue.500"
        size="xl"
      />
    )
  }

  if (errorMessage) {
    return (
      <Alert status="error" mt={8}>
        <AlertIcon />
        {errorMessage}
      </Alert>
    )
  }

  if (me.role === 'USER') {
    return (
      <Center>
        <HStack mt={8} gap={2}>
          <Text fontSize="xl">You don't have admin permissions</Text>
          <IconButton
            aria-label="Home"
            onClick={() => navigate('/')}
            icon={<AiOutlineHome />}
          />
        </HStack>
      </Center>
    )
  }

  return <NewRoom />
}

export default Admin
