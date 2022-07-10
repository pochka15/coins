import React from 'react'
import { Box } from '@chakra-ui/react'

/**
 * Card representing task
 * @return {JSX.Element}
 * @constructor
 */
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
