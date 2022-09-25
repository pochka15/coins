import React from 'react'
import {
  CloseButton,
  Flex,
  HStack,
  IconButton,
  Input,
  InputGroup,
  InputRightElement
} from '@chakra-ui/react'
import { AddIcon } from '@chakra-ui/icons'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import { FiLogIn, FiLogOut } from 'react-icons/fi'
import auth from '../security/auth'
import { useCurrentRoom } from '../hooks/use-current-room'

function HomeLayout({
  onAddNewTask,
  children,
  searchbarValue,
  onSearchbarValueChange
}) {
  const room = useCurrentRoom()
  return (
    <>
      <Flex marginTop={4} alignItems="center" justify="center">
        <HStack>
          <InputGroup>
            <Input
              width={['3xs', 'sm', 'md', 'xl']}
              boxShadow="base"
              placeholder="Find task by name"
              value={searchbarValue}
              onChange={x => onSearchbarValueChange(x.target.value)}
            />

            <InputRightElement hidden={searchbarValue === ''}>
              <CloseButton onClick={() => onSearchbarValueChange('')} />
            </InputRightElement>
          </InputGroup>

          {auth.isLogged() ? (
            <>
              <IconButton
                aria-label="Add new task"
                icon={<AddIcon />}
                onClick={onAddNewTask}
                disabled={room === null}
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
