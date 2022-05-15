import React from 'react'
import {
  ChakraProvider,
  theme,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel
} from '@chakra-ui/react'
import Tmp from './Tmp'
import Home from './Home'

function App() {
  return (
    <ChakraProvider theme={theme}>
      <Tabs>
        <TabList>
          <Tab>Home</Tab>
          <Tab>Tmp</Tab>
        </TabList>

        <TabPanels>
          <TabPanel>
            <Home />
          </TabPanel>

          <TabPanel>
            <Tmp />
          </TabPanel>
        </TabPanels>
      </Tabs>
    </ChakraProvider>
  )
}

export default App
