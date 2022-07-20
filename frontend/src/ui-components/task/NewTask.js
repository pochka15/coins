import React, { useState } from 'react'
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
import { createTask } from '../../api/tasks'
import { TASKS_QUERY_KEY } from '../Home'

/**
 *
 * @param {Date} date
 * @return {string} YYYY-MM-DD
 */
function formatDeadline(date) {
  return date.toISOString().substring(0, 10)
}

/**
 * Component that wraps and submits task form
 * @param {boolean} isOpen
 * @param {function(): void} onClose
 * @return {JSX.Element}
 * @constructor
 */
function NewTask({ isOpen, onClose }) {
  const [taskErrors, setTaskErrors] = useState(/** @type {FieldError[]} */ [])

  const mutation = useMutation(
    /** @param {TNewTask} task */
    task => createTask({ ...task, deadline: formatDeadline(task.deadline) }),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(TASKS_QUERY_KEY).then(() => onClose())
        setTaskErrors([])
      },
      onError: e => {
        if (e.response.status === 400) {
          setTaskErrors(e.response.data.errors)
        }
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
          <TaskForm
            onSubmit={task => mutation.mutate(task)}
            errors={taskErrors}
          />
        </ModalBody>
        <ModalFooter>
          {mutation.isError && (
            <Alert status="error">
              <AlertIcon />
              {mutation.error.response.status === 403
                ? "You don't have permissions to create a task"
                : `There was an error when creating a new task. ${mutation.error.message}`}
            </Alert>
          )}
        </ModalFooter>
      </ModalContent>
    </Modal>
  )
}

export default NewTask
