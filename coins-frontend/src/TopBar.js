import React from 'react'
import {
  Button,
  Grid,
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
  useDisclosure
} from '@chakra-ui/react'
import { AddIcon } from '@chakra-ui/icons'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import TaskForm from './task/TaskForm'

function TopBar() {
  const { isOpen, onOpen, onClose } = useDisclosure()
  return (
    <>
      <HStack>
        <Input placeholder="Find anything" size="md" />
        <IconButton
          aria-label="Add new task"
          icon={<AddIcon />}
          onClick={onOpen}
        />
        <Grid p={3}>
          <ColorModeSwitcher justifySelf="flex-end" />
        </Grid>
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
    </>
  )
}

export default TopBar
