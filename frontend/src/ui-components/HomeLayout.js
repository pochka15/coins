import React from 'react'
import { Flex, HStack, IconButton, Input } from '@chakra-ui/react'
import { AddIcon } from '@chakra-ui/icons'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import { FiLogIn, FiLogOut } from 'react-icons/fi'
import auth from '../security/auth'

function HomeLayout({ onAddNewTask }) {
  return (
    <Flex marginTop={8} alignItems="center" justify="center">
      <HStack>
        <Input boxShadow="base" w={600} placeholder="Find anything" size="md" />
        {auth.isLogged() ? (
          <>
            <IconButton
              aria-label="Add new task"
              icon={<AddIcon />}
              onClick={onAddNewTask}
            />
            <IconButton
              aria-label={'Log out'}
              onClick={() => auth.logout()}
              icon={<FiLogOut />}
            />
          </>
        ) : (
          <IconButton
            aria-label={'Log in'}
            onClick={() => auth.startLogin()}
            icon={<FiLogIn />}
          />
        )}
        <ColorModeSwitcher />
      </HStack>
    </Flex>
  )
}

export default HomeLayout
