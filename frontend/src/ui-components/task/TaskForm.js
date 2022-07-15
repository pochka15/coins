import React, { useEffect } from 'react'
import {
  FormControl,
  FormLabel,
  NumberInput,
  Input,
  NumberInputField,
  Textarea,
  Container,
  Button,
  FormErrorMessage
} from '@chakra-ui/react'
import { useForm } from 'react-hook-form'
import { GLOBAL_ROOM_ID } from '../Home'

/**
 *
 * @param {function(TNewTask): void} onSubmit
 * @return {JSX.Element}
 * @constructor
 */
function TaskForm({ onSubmit }) {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm()

  useEffect(() => {
    register('deadline', { value: '2023-12-03' })
    register('roomId', { value: GLOBAL_ROOM_ID })
    register('userId', { value: '705b19c5-322f-40cf-9216-244d22bf4d2e' })
  }, [register])

  // noinspection JSValidateTypes
  return (
    <Container maxW="md">
      <form onSubmit={handleSubmit(onSubmit)}>
        <FormControl mb={8}>
          <FormLabel htmlFor="title">Title</FormLabel>
          <Input
            id="title"
            type="text"
            {...register('title', { minLength: 1, required: true })}
          />
          <FormErrorMessage>
            {errors.title && errors.title.message}
          </FormErrorMessage>
        </FormControl>

        <FormControl mb={8}>
          <FormLabel htmlFor="content">Content</FormLabel>
          <Textarea id="content" placeholder="" {...register('content')} />
        </FormControl>

        <FormControl mb={8}>
          <FormLabel htmlFor="budget">Budget</FormLabel>
          <NumberInput>
            <NumberInputField
              id="budget"
              {...register('budget', { min: 0, required: true })}
            />
          </NumberInput>
        </FormControl>

        <Button mt={4} type="submit" isLoading={isSubmitting}>
          Submit
        </Button>
      </form>
    </Container>
  )
}

export default TaskForm
