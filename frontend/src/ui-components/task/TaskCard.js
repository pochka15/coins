import React from 'react'
import { Box, Flex, Heading, HStack, Icon, Text } from '@chakra-ui/react'
import { GiTwoCoins } from 'react-icons/gi'

/**
 * Card representing task
 * @param {Task} task
 * @return {JSX.Element}
 * @constructor
 */
function TaskCard({ task }) {
  return (
    <Box
      w="100%"
      p={8}
      maxW="3xl"
      borderWidth="1px"
      borderRadius="lg"
      overflow="hidden"
    >
      <Heading as="h3" size="lg" noOfLines={1}>
        {`${task.title}`}
      </Heading>
      <Flex justifyContent="space-between" alignItems="center" marginTop={4}>
        <Text>Deadline: {task.deadline}</Text>
        <Text>Author: {task.author}</Text>
        {task.assignee ? (
          <Text as="b">Assignee: {task.assignee}</Text>
        ) : (
          <Text>Unassigned</Text>
        )}
        <HStack>
          <Text>Reward: {task.budget}</Text>
          <Icon as={GiTwoCoins} />
        </HStack>
      </Flex>
    </Box>
  )
}

export default TaskCard
