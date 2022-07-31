import React, { useState } from 'react'
import RoomForm from './RoomForm'
import { useMutation, useQuery } from 'react-query'
import { createRoom } from '../../api/admin'
import { Alert, AlertIcon, Container, Spinner } from '@chakra-ui/react'
import {
  extractErrorMessage,
  extractValidationErrors
} from '../../api/api-utils'
import { getCourseEdition } from '../../api/usos'
import { UsosLabel } from '../Home'

function NewRoom() {
  const [room, setRoom] = useState(null)

  const mutation = useMutation(
    /** @param {ApiNewRoom} room */
    room => createRoom(room),
    {
      onSuccess: room => setRoom(room)
    }
  )

  // We use this 'fake' query to check if our usos access token is not expired
  // If we get 403 then we should login again
  const { isLoading, isError, error } = useQuery(
    ['course-edition'],
    () => getCourseEdition('', ''),
    { retry: false, refetchOnWindowFocus: false, refetchOnMount: true }
  )

  const isTokenExpired = isError && error.response.status === 403

  /** @param {ApiNewRoom} room */
  const submit = room => mutation.mutate(room)

  const getErrorMessage = () => {
    const validationErrors = extractValidationErrors(mutation.error) || []
    if (validationErrors.length === 0) {
      return (
        extractErrorMessage(mutation.error) ||
        'Unknown error occurred when trying to create a room'
      )
    }
    return validationErrors
      .map(error => `${error.fieldName}: ${error.message || 'incorrect'}`)
      .join('\n')
  }

  if (isLoading) {
    return (
      <Container maxW="md">
        <Spinner
          thickness="4px"
          speed="0.65s"
          emptyColor="gray.200"
          color="blue.500"
          size="xl"
        />
      </Container>
    )
  }

  if (isTokenExpired) {
    return (
      <Container maxW="md" mt={8}>
        <Alert status="warning" mt={8}>
          <AlertIcon />
          {extractErrorMessage(error) ||
            'Unknown error is occurred. Try to login again'}
        </Alert>
        <UsosLabel mt={8} />
      </Container>
    )
  }

  return (
    <Container maxW="md" mt={8}>
      <RoomForm onSubmit={submit} />
      {room && (
        <Alert status="success" mt={8}>
          <AlertIcon />
          Room '{room.name}' has been created successfully
        </Alert>
      )}
      {mutation.isError && (
        <Alert status="error" mt={8}>
          <AlertIcon />
          {getErrorMessage()}
        </Alert>
      )}
    </Container>
  )
}

export default NewRoom
