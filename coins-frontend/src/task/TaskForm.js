import React from 'react'
import {
  FormControl,
  FormLabel,
  NumberInput,
  Input,
  NumberInputField,
  Textarea,
  Container
} from '@chakra-ui/react'

function TaskForm() {
  return (
    <Container maxW="md">
      <form>
        <FormControl mb={8}>
          <FormLabel htmlFor="title">Title</FormLabel>
          <Input id="title" type="text" />
        </FormControl>
        <FormControl mb={8}>
          <FormLabel htmlFor="content">Content</FormLabel>
          <Textarea id="content" placeholder="Here is a sample placeholder" />
        </FormControl>
        <FormControl mb={8}>
          <FormLabel htmlFor="budget">Budget</FormLabel>
          <NumberInput>
            <NumberInputField />
          </NumberInput>
        </FormControl>
      </form>
    </Container>
  )
}

export default TaskForm
