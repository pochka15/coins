import React from 'react'
import { ChakraProvider, theme, useDisclosure } from '@chakra-ui/react'
import Home from './ui-components/Home'
import HomeLayout from './ui-components/HomeLayout'
import NewTask from './ui-components/task/NewTask'

function App() {
  const { isOpen, onOpen, onClose } = useDisclosure()

  return (
    <ChakraProvider theme={theme}>
      <HomeLayout onAddNewTask={onOpen}>
        <Home />
        <NewTask isOpen={isOpen} onClose={onClose} />
      </HomeLayout>
    </ChakraProvider>
  )
}

export default App
