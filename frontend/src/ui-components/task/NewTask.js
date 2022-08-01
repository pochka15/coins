import React from 'react'
import { useMutation, useQueryClient } from 'react-query'
import {
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
  extractValidationErrors,
  toErrorMessage
} from '../../api/api-utils'
import { WALLET_KEY } from '../wallet/CoinsSummary'
import { useCurrentRoom } from '../../hooks/use-current-room'

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
 * Component that wraps and submits task form
 * @param {boolean} isOpen
 * @param {function(): void} onClose
 * @param {function(TNotification): void} onNotificationChange
 * @return {JSX.Element}
 * @constructor
 */
function NewTask({ isOpen, onClose, onNotification }) {
  const queryClient = useQueryClient()
  const room = useCurrentRoom()

  const mutation = useMutation(
    /** @param {TNewTask} task */ task => {
      onClose()
      onNotification({
        type: 'loading',
        payload: { message: `Uploading ${task.title}` }
      })
      return createTask(toApiTask(task, room))
    },
    {
      onSuccess: task => {
        queryClient
          .invalidateQueries(TASKS_QUERY_KEY)
          .then(() => queryClient.invalidateQueries(WALLET_KEY))
          .then(() =>
            onNotification({
              type: 'success',
              payload: { message: `Created task '${task.title}'` }
            })
          )
      },
      onError: e => {
        const validationErrors = extractValidationErrors(e) || []
        const message =
          toErrorMessage(validationErrors) ||
          extractErrorMessage(e) ||
          `There was an error when creating a new task`
        onNotification({ type: 'error', payload: { message } })
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
            isLoading={mutation.isLoading}
          />
        </ModalBody>
        <ModalFooter />
      </ModalContent>
    </Modal>
  )
}

export default NewTask
