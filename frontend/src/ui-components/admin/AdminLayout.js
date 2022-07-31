import React from 'react'
import { Flex, HStack, IconButton, Input } from '@chakra-ui/react'
import { FiLogIn, FiLogOut } from 'react-icons/fi'
import { useNavigate } from 'react-router-dom'
import auth from '../../security/auth'
import { ColorModeSwitcher } from '../ColorModeSwitcher'
import { AiOutlineHome } from 'react-icons/ai'

function AdminLayout({ children }) {
  const nav = useNavigate()

  return (
    <>
      <Flex marginTop={4} alignItems="center" justify="center">
        <HStack>
          <Input
            width={['3xs', 'sm', 'md', 'xl']}
            boxShadow="base"
            placeholder="Find anything"
            disabled={true}
          />
          {auth.isLogged() ? (
            <>
              <IconButton
                aria-label="Home"
                icon={<AiOutlineHome />}
                onClick={() => nav('/')}
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

export default AdminLayout
