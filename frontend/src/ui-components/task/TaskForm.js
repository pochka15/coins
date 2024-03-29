import React from 'react'
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
import { CustomDatePicker } from './CustomDatePicker'

/**
 *
 * @param {function(TNewTask): void} onSubmit
 * @param isLoading
 * @return {JSX.Element}
 * @constructor
 */
function TaskForm({ onSubmit, isLoading }) {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors: formErrors }
  } = useForm()

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

        <FormControl mb={8} isInvalid={formErrors.budget}>
          <FormLabel htmlFor="budget">Budget</FormLabel>
          <NumberInput>
            <NumberInputField
              id="budget"
              {...register('budget', {
                validate: value => {
                  return !isNaN(value) && value >= 0
                },
                required: true,
                setValueAs: parseInt
              })}
            />
          </NumberInput>
          <FormErrorMessage>
            {formErrors.budget && 'Budget must be a non-negative value'}
          </FormErrorMessage>
        </FormControl>

        <FormControl mb={8} isInvalid={formErrors.deadline}>
          <FormLabel htmlFor="deadline">Deadline (optional)</FormLabel>
          <Controller
            control={control}
            name="deadline"
            defaultValue={null}
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

        <Button mt={4} type="submit" isLoading={isLoading}>
          Submit
        </Button>
      </form>
    </Container>
  )
}

export default TaskForm
