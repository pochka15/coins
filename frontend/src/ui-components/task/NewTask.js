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
import { TASKS_QUERY_KEY } from '../TasksFeed'
import {
  extractErrorMessage,
  extractValidationErrors
} from '../../api/api-utils'
import { WALLET_KEY } from '../wallet/CoinsSummary'
import { useCurrentRoom } from '../../hooks/use-current-room'

/**
 * @typedef {{
 *   formErrors: TFieldError[],
 *   errorMessage: string
 * }} TErrorsContainer
 */

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
 * @param {ApiRoom} room
 * @return {ApiNewTask}
 */
function toApiTask(task, room) {
  return {
    title: task.title,
    content: task.content,
    deadline: formatDeadline(task.deadline),
    budget: task.budget,
    roomId: room.id
  }
}

/**
 * @return {TErrorsContainer}
 * @constructor
 */
function ErrorsContainer() {
  return { formErrors: [], errorMessage: '' }
}

/**
 * Component that wraps and submits task form
 * @param {boolean} isOpen
 * @param {function(): void} onClose
 * @return {JSX.Element}
 * @constructor
 */
function NewTask({ isOpen, onClose }) {
  const [container, setContainer] = useState(ErrorsContainer())
  const queryClient = useQueryClient()
  const room = useCurrentRoom()

  const mutation = useMutation(
    /** @param {TNewTask} task */ task => createTask(toApiTask(task, room)),
    {
      onSuccess: () => {
        queryClient
          .invalidateQueries(TASKS_QUERY_KEY)
          .then(() => queryClient.invalidateQueries(WALLET_KEY))
          .then(() => onClose())
        setContainer(ErrorsContainer())
      },
      onError: e => {
        setContainer({
          formErrors: extractValidationErrors(e) || [],
          errorMessage:
            e.response.status === 403
              ? "You don't have permissions to create a task"
              : extractErrorMessage(e) ||
                `There was an error when creating a new task`
        })
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
            errors={container.formErrors}
            isLoading={mutation.isLoading}
          />
        </ModalBody>
        <ModalFooter>
          {container.errorMessage && (
            <Alert status="error">
              <AlertIcon />
              {container.errorMessage}
            </Alert>
          )}
        </ModalFooter>
      </ModalContent>
    </Modal>
  )
}

export default NewTask
