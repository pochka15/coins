import React, { useState } from 'react'
import { useDisclosure } from '@chakra-ui/react'
import Home from './ui-components/Home'
import HomeLayout from './ui-components/HomeLayout'
import NewTask from './ui-components/task/NewTask'

function App() {
  const [notification, setNotification] = useState(null)
  const { isOpen, onOpen, onClose } = useDisclosure()

  return (
    <HomeLayout onAddNewTask={onOpen}>
      <Home notification={notification} />
      <NewTask
        isOpen={isOpen}
        onClose={onClose}
        onNotification={setNotification}
      />
    </HomeLayout>
  )
}

export default App
