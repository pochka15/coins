import React from 'react'
import {
  Button,
  HStack,
  IconButton,
  Input,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  useDisclosure,
  VStack
} from '@chakra-ui/react'
import { AddIcon } from '@chakra-ui/icons'
import TaskCard from './task/TaskCard'
import TaskForm from './task/TaskForm'

function Home() {
  const { isOpen, onOpen, onClose } = useDisclosure()
  const cardIndexes = Array.from(Array(3).keys())

  return (
    <>
      <HStack marginBottom={8}>
        <Input placeholder="Find anything" size="md" />
        <IconButton
          aria-label="Add new task"
          icon={<AddIcon />}
          onClick={onOpen}
        />
      </HStack>
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>New task</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <TaskForm />
          </ModalBody>
          <ModalFooter>
            <Button colorScheme="blue" mr={3} onClick={onClose}>
              Close
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
      <VStack>
        {cardIndexes.map(ind => (
          <TaskCard key={ind}>Task {ind}</TaskCard>
        ))}
      </VStack>
    </>
  )
}

export default Home
