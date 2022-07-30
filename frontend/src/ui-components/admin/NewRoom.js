import React, { useState } from 'react'
import RoomForm from './RoomForm'
import { useMutation } from 'react-query'
import { createRoom } from '../../api/admin'
import { Alert, AlertIcon, Container } from '@chakra-ui/react'
import {
  extractErrorMessage,
  extractValidationErrors
} from '../../api/api-utils'

function NewRoom() {
  const [room, setRoom] = useState(null)

  const mutation = useMutation(
    /** @param {ApiNewRoom} room */
    room => createRoom(room),
    {
      onSuccess: room => setRoom(room)
    }
  )

  /**@param {ApiNewRoom} room */
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

  return (
    <Container maxW="md" mt={8}>
      <RoomForm onSubmit={submit} />
      {room && (
        <Alert status="success" mt={8}>
          <AlertIcon />
          Room {room.name} has been created successfully
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
