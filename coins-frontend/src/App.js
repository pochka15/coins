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
import Home from './ui-components/Home'
import HomeLayout from './ui-components/HomeLayout'

function App() {
  return (
    <ChakraProvider theme={theme}>
      <HomeLayout>
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
      </HomeLayout>
    </ChakraProvider>
  )
}

export default App
