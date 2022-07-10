import React from 'react'
import { HStack, IconButton, Input, useDisclosure } from '@chakra-ui/react'
import { AddIcon } from '@chakra-ui/icons'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import { FiLogIn, FiLogOut } from 'react-icons/fi'
import auth from '../security/auth'
import NewTask from './task/NewTask'

function HomeLayout({ children }) {
  const { isOpen, onOpen, onClose } = useDisclosure()
  return (
    <>
      <HStack>
        <Input placeholder="Find anything" size="md" />
        <ColorModeSwitcher />
        {auth.isLogged() ? (
          <>
            <IconButton
              aria-label={'Log out'}
              onClick={() => auth.logout()}
              icon={<FiLogOut />}
            />
            <IconButton
              aria-label="Add new task"
              icon={<AddIcon />}
              onClick={onOpen}
            />
          </>
        ) : (
          <IconButton
            aria-label={'Log in'}
            onClick={() => auth.startLogin()}
            icon={<FiLogIn />}
          />
        )}
      </HStack>
      <NewTask isOpen={isOpen} onClose={onClose} />
      {children}
    </>
  )
}

export default HomeLayout
