import React, { createContext, useState } from 'react'
import { useDisclosure } from '@chakra-ui/react'
import Home from './ui-components/Home'
import HomeLayout from './ui-components/HomeLayout'
import NewTask from './ui-components/task/NewTask'
import ContributionMessage from './ui-components/ContributionMessage'

export const SearchbarContext = createContext('')

function App() {
  const [searchbarValue, setSearchbarValue] = useState('')
  const [notification, setNotification] = useState(null)
  const { isOpen, onOpen, onClose } = useDisclosure()

  return (
    <SearchbarContext.Provider value={searchbarValue}>
      <HomeLayout
        onAddNewTask={onOpen}
        searchbarValue={searchbarValue}
        onSearchbarValueChange={setSearchbarValue}
      >
        <Home notification={notification} />

        <NewTask
          isOpen={isOpen}
          onClose={onClose}
          onNotification={setNotification}
        />
      </HomeLayout>

      <ContributionMessage />
    </SearchbarContext.Provider>
  )
}

export default App
