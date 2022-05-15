import React from 'react'
import { Box } from '@chakra-ui/react'

function TaskCard(props) {
  return (
    <Box
      w="100%"
      p={8}
      maxW="sm"
      borderWidth="1px"
      borderRadius="lg"
      overflow="hidden"
    >
      {props.children}
    </Box>
  )
}

export default TaskCard
