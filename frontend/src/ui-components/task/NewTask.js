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
import { GLOBAL_ROOM_ID, TASKS_QUERY_KEY } from '../TasksFeed'
import { extractErrorMessage } from '../../api/api-utils'

/**
 *
 * @param {Date} date
 * @return {string} YYYY-MM-DD
 */
function formatDeadline(date) {
  return date.toISOString().substring(0, 10)
}

/**
 * Converter
 * @param {TNewTask} task
 * @return {ApiNewTask}
 */
function toApiTask(task) {
  return {
    title: task.title,
    content: task.content,
    deadline: formatDeadline(task.deadline),
    budget: task.budget,
    roomId: GLOBAL_ROOM_ID
  }
}

/**
 * Component that wraps and submits task form
 * @param {boolean} isOpen
 * @param {function(): void} onClose
 * @return {JSX.Element}
 * @constructor
 */
function NewTask({ isOpen, onClose }) {
  const [taskErrors, setTaskErrors] = useState(/** @type {TFieldError[]} */ [])
  const queryClient = useQueryClient()

  const mutation = useMutation(
    /** @param {TNewTask} task */ task => createTask(toApiTask(task)),
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
            isLoading={mutation.isLoading}
          />
        </ModalBody>
        <ModalFooter>
          {mutation.isError && (
            <Alert status="error">
              <AlertIcon />
              {mutation.error.response.status === 403
                ? "You don't have permissions to create a task"
                : extractErrorMessage(mutation.error) ||
                  `There was an error when creating a new task`}
            </Alert>
          )}
        </ModalFooter>
      </ModalContent>
    </Modal>
  )
}

export default NewTask
