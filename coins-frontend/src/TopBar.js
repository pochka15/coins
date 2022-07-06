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
  useDisclosure
} from '@chakra-ui/react'
import { AddIcon } from '@chakra-ui/icons'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import TaskForm from './task/TaskForm'
import { FiLogIn, FiLogOut } from 'react-icons/fi'
import auth from './auth'

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
        <ColorModeSwitcher />
        {auth.isLogged() ? (
          <IconButton
            aria-label={'Log out'}
            onClick={() => auth.logout()}
            icon={<FiLogOut />}
          />
        ) : (
          <IconButton
            aria-label={'Log in'}
            onClick={() => auth.startLogin()}
            icon={<FiLogIn />}
          />
        )}
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
