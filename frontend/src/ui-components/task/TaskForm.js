import React, { useEffect } from 'react'
import {
  Button,
  Container,
  FormControl,
  FormErrorMessage,
  FormLabel,
  Input,
  NumberInput,
  NumberInputField,
  Textarea
} from '@chakra-ui/react'
import { Controller, useForm } from 'react-hook-form'
import { GLOBAL_ROOM_ID } from '../Home'
import { CustomDatePicker } from './CustomDatePicker'

/**
 *
 * @param {function(TNewTask): void} onSubmit
 * @param {FieldError[]} errors
 * @return {JSX.Element}
 * @constructor
 */
function TaskForm({ onSubmit, errors }) {
  const {
    register,
    handleSubmit,
    control,
    setError,
    formState: { errors: formErrors, isSubmitting }
  } = useForm({
    defaultValues: { roomId: GLOBAL_ROOM_ID }
  })

  useEffect(() => {
    for (const error of errors) {
      setError(error.fieldName, { message: error.message })
    }
  }, [errors])

  // noinspection JSValidateTypes
  return (
    <Container maxW="md">
      <form onSubmit={handleSubmit(onSubmit)}>
        <FormControl mb={8} isInvalid={formErrors.title}>
          <FormLabel htmlFor="title">Title</FormLabel>
          <Input
            id="title"
            type="text"
            {...register('title', { minLength: 1, required: true })}
          />
          <FormErrorMessage>Title is required</FormErrorMessage>
        </FormControl>

        <FormControl mb={8}>
          <FormLabel htmlFor="content">Content</FormLabel>
          <Textarea id="content" placeholder="" {...register('content')} />
        </FormControl>

        <FormControl mb={8} isInvalid={formErrors.deadline}>
          <FormLabel htmlFor="deadline">Deadline</FormLabel>
          <Controller
            control={control}
            name="deadline"
            defaultValue={new Date()}
            render={({ field: { onChange, onBlur, value } }) => (
              <CustomDatePicker
                onChange={x => onChange(x)}
                onBlur={onBlur}
                selectedDate={value}
              />
            )}
          />
          <FormErrorMessage>{formErrors.deadline?.message}</FormErrorMessage>
        </FormControl>

        <FormControl mb={8} isInvalid={formErrors.budget}>
          <FormLabel htmlFor="budget">Budget</FormLabel>
          <NumberInput>
            <NumberInputField
              id="budget"
              {...register('budget', { min: 0, required: true })}
            />
          </NumberInput>
          <FormErrorMessage>
            Budget must be a non-negative value
          </FormErrorMessage>
        </FormControl>

        <Button mt={4} type="submit" isLoading={isSubmitting}>
          Submit
        </Button>
      </form>
    </Container>
  )
}

export default TaskForm
