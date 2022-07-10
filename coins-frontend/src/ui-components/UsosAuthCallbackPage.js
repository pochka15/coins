import React, { useEffect } from 'react'
import auth from '../security/auth'
import { useNavigate } from 'react-router-dom'
import { Spinner } from '@chakra-ui/react'

function UsosAuthCallbackPage() {
  const navigate = useNavigate()

  useEffect(() => {
    auth.finishLogin().then(() => navigate('/'))
  }, [navigate])

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

export default UsosAuthCallbackPage
