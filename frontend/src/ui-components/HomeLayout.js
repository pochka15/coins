import React from 'react'
import { Flex, HStack, IconButton, Input } from '@chakra-ui/react'
import { AddIcon } from '@chakra-ui/icons'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import { FiLogIn, FiLogOut } from 'react-icons/fi'
import auth from '../security/auth'

function HomeLayout({ onAddNewTask, children }) {
  return (
    <>
      <Flex marginTop={4} alignItems="center" justify="center">
        <HStack>
          <Input
            width={['3xs', 'sm', 'md', 'xl']}
            boxShadow="base"
            placeholder="Find anything"
          />
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
      {children}
    </>
  )
}

export default HomeLayout
