import React from 'react'
import { useMutation, useQueryClient } from 'react-query'
import {
  Alert,
  AlertIcon,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay
} from '@chakra-ui/react'
import TaskForm from './TaskForm'
import { createTask } from '../api/tasks'
import { TASKS_QUERY_KEY } from '../Home'

/**
 * Component that wraps and submits task form
 * @param {boolean} isOpen
 * @param {function(): void} onClose
 * @return {JSX.Element}
 * @constructor
 */
function NewTask({ isOpen, onClose }) {
  const mutation = useMutation(
    /** @param {NewTask} task */
    task => createTask(task),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(TASKS_QUERY_KEY).then(() => onClose())
      }
    }
  )

  const queryClient = useQueryClient()

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>New task</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <TaskForm onSubmit={task => mutation.mutate(task)} />
        </ModalBody>
        <ModalFooter>
          {mutation.isError && (
            <Alert status="error">
              <AlertIcon />
              There was an error when creating a new task: {mutation.error.message}
            </Alert>
          )}
        </ModalFooter>
      </ModalContent>
    </Modal>
  )
}

export default NewTask
