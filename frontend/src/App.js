import React from 'react'
import { ChakraProvider, theme } from '@chakra-ui/react'
import Home from './ui-components/Home'
import HomeLayout from './ui-components/HomeLayout'

function App() {
  return (
    <ChakraProvider theme={theme}>
      <HomeLayout>
        <Home />
      </HomeLayout>
    </ChakraProvider>
  )
}

export default App
