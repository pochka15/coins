import React, { useEffect } from 'react'
import auth from '../security/auth'
import { useNavigate } from 'react-router-dom'
import { Center, Container, Spinner } from '@chakra-ui/react'

function UsosAuthCallbackPage() {
  const navigate = useNavigate()

  useEffect(() => {
    auth.finishLogin().then(() => navigate('/'))
  }, [navigate])

  return (
    <Container maxW="sm" mt={8}>
      <Center>
        <Spinner
          thickness="4px"
          speed="0.65s"
          emptyColor="gray.200"
          color="blue.500"
          size="xl"
        />
      </Center>
    </Container>
  )
}

export default UsosAuthCallbackPage
