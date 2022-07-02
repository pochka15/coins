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
import TopBar from './TopBar'

function App() {
  return (
    <ChakraProvider theme={theme}>
      <TopBar />
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
