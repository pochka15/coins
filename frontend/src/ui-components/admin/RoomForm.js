import React, { useEffect, useMemo, useState } from 'react'
import { useForm } from 'react-hook-form'
import {
  Alert,
  AlertIcon,
  Box,
  Button,
  Checkbox,
  Text,
  Drawer,
  DrawerBody,
  DrawerCloseButton,
  DrawerContent,
  DrawerHeader,
  DrawerOverlay,
  Flex,
  FormControl,
  FormErrorMessage,
  FormHelperText,
  FormLabel,
  Input,
  ListItem,
  NumberInput,
  NumberInputField,
  Spinner,
  Stack,
  UnorderedList,
  useBoolean,
  useColorModeValue,
  useDisclosure
} from '@chakra-ui/react'
import { Select } from 'chakra-react-select'
import { useDebounce } from '../../hooks/use-debounce-component-value'
import { useQuery } from 'react-query'
import { getCourseEdition, getCourseIdsByName } from '../../api/usos'
import auth from '../../security/auth'

const getErrorMessage = error => error.response.data?.message
const semesterPattern = /20\d{2}[LZ]/

/**
 * Converter
 * @param participant
 * @return {ParticipantWithState}
 */
function toParticipantWithState(participant) {
  return { ...participant, isEnabled: true }
}

const Course = ({ onCourseIdChange }) => {
  const [name, setName] = useState('')
  const dName = useDebounce(name, 500)
  const [selectedOption, setSelectedOption] = useState(null)
  const isDebounced = dName !== name

  const { data, isFetching, error, isError } = useQuery(
    ['course', dName],
    () => getCourseIdsByName(dName),
    {
      staleTime: 5 * 60 * 1000, // 5 min
      placeholderData: []
    }
  )

  const options = useMemo(() => data.map(id => ({ id })), [data])

  const handleInputChange = (inputValue, event) => {
    // prevent outside click from resetting inputValue to ""
    if (event.action !== 'input-blur' && event.action !== 'menu-close') {
      setName(inputValue)
    }
  }

  const getOptionValue = () => {
    // We return name in order to have proper selection options
    // otherwise we have an empty array of options in a select component
    return name
  }

  return (
    <FormControl mt={8} id="course" isInvalid={isError}>
      <FormLabel>Course</FormLabel>

      <Select
        name="Course"
        isClearable={true}
        escapeClearsValue={true}
        noOptionsMessage={() => 'No courses found'}
        placeholder={'Search for a course'}
        isLoading={isFetching || isDebounced}
        inputValue={name}
        onInputChange={handleInputChange}
        value={selectedOption}
        options={options}
        onChange={option => {
          onCourseIdChange(option?.id || '')
          setSelectedOption(option)
        }}
        getOptionValue={getOptionValue}
        getOptionLabel={x => x.id}
      />

      <FormHelperText>Search for a course by an id or name</FormHelperText>
      <FormErrorMessage>
        {isError &&
          (getErrorMessage(error) ||
            "Couldn't fetch courses, unknown error occurred")}
      </FormErrorMessage>
    </FormControl>
  )
}

function ParticipantsDrawer({
  participants,
  onChangeParticipants,
  isOpen,
  onClose
}) {
  const [someEnabled, allEnabled] = useMemo(() => {
    return [
      participants.some(x => x.isEnabled),
      participants.every(x => x.isEnabled)
    ]
  }, [participants])

  return (
    <Drawer isOpen={isOpen} placement="right" onClose={onClose}>
      <DrawerOverlay />
      <DrawerContent>
        <DrawerCloseButton />
        <DrawerHeader>Participants</DrawerHeader>

        <DrawerBody>
          <Checkbox
            isChecked={allEnabled}
            isIndeterminate={someEnabled && !allEnabled}
            onChange={e => {
              onChangeParticipants(participants => {
                return participants.map(x => ({
                  ...x,
                  isEnabled: e.target.checked
                }))
              })
            }}
          >
            {allEnabled ? 'Remove all' : 'Add all'}
          </Checkbox>
          <Stack pl={6} mt={1} spacing={1}>
            {participants.map((x, ind) => {
              return (
                <Checkbox
                  key={x.id}
                  isChecked={participants[ind].isEnabled}
                  onChange={e => {
                    onChangeParticipants(() => {
                      const next = participants.slice()
                      next[ind] = {
                        ...next[ind],
                        isEnabled: e.target.checked
                      }
                      return next
                    })
                  }}
                >
                  {`${x.firstName} ${x.lastName}`}
                </Checkbox>
              )
            })}
          </Stack>
        </DrawerBody>
      </DrawerContent>
    </Drawer>
  )
}

function ParticipantsPreview({ participants, onOpen }) {
  const [isHovering, setIsHovering] = useBoolean()
  const hoverColor = useColorModeValue('blackAlpha.50', 'whiteAlpha.100')
  const color = isHovering ? hoverColor : ''

  const [enabledParticipants, allDisabled] = useMemo(() => {
    const enabledParticipants = participants.filter(x => x.isEnabled)
    const allDisabled = enabledParticipants.length === 0
    return [enabledParticipants, allDisabled]
  }, [participants])

  return (
    <Box
      onClick={onOpen}
      borderWidth="1px"
      borderRadius="lg"
      p={4}
      bgColor={color}
      onMouseEnter={setIsHovering.on}
      onMouseLeave={setIsHovering.off}
      cursor={isHovering ? 'pointer' : 'default'}
    >
      {allDisabled ? (
        <Text>No participant is added to this room</Text>
      ) : (
        <UnorderedList>
          {enabledParticipants.slice(0, 3).map(x => (
            <ListItem key={x.id}>
              {x.firstName} {x.lastName}
            </ListItem>
          ))}
          {enabledParticipants.length > 3 && <ListItem key={4}>...</ListItem>}
        </UnorderedList>
      )}
    </Box>
  )
}

function Participants({ courseId, semester, onParticipantsChange }) {
  const enabled = courseId !== '' && semester !== ''
  const { isOpen, onOpen, onClose } = useDisclosure()

  const [participants, setParticipants] = useState(
    /** @type {ParticipantWithState[]} */
    []
  )

  const updateParticipants = next => {
    setParticipants(next)
    onParticipantsChange(next)
  }

  const { data, isFetching, isError, error } = useQuery(
    ['course-edition', courseId, semester],
    () => getCourseEdition(courseId, semester),
    {
      enabled,
      staleTime: 5 * 60 * 1000, // 5 min
      retry: false,
      onError(e) {
        if (e.response.status === 403) auth.startLogin()
      }
    }
  )

  useEffect(() => {
    updateParticipants(
      isFetching || !data
        ? []
        : data.lecturers.concat(data.participants).map(toParticipantWithState)
    )
  }, [data, isFetching])

  if (!enabled) return null

  if (isFetching)
    return (
      <Spinner
        thickness="4px"
        speed="0.65s"
        emptyColor="gray.200"
        color="blue.500"
        mt={8}
      />
    )

  if (participants.length === 0 && !isError) {
    return (
      <Alert status="info" mt={8}>
        <AlertIcon />
        This course edition has no students
      </Alert>
    )
  }

  return (
    <FormControl isInvalid={isError} mt={8}>
      <FormLabel>Participants</FormLabel>

      <ParticipantsPreview participants={participants} onOpen={onOpen} />

      <ParticipantsDrawer
        participants={participants}
        onChangeParticipants={updateParticipants}
        isOpen={isOpen}
        onClose={onClose}
      />

      <FormErrorMessage>
        {isError &&
          (getErrorMessage(error) ||
            "Couldn't fetch courseEdition, unknown error occurred")}
      </FormErrorMessage>
    </FormControl>
  )
}

/**
 * Form to create a room
 * @param {function(ApiNewRoom): void} onSubmit
 * @return {JSX.Element}
 * @constructor
 */
function RoomForm({ onSubmit }) {
  const [courseId, setCourseId] = useState('')

  const [participants, setParticipants] = useState(
    /** @type {ParticipantWithState[]} */
    []
  )

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting }
  } = useForm()

  const submit = data => {
    onSubmit({
      name: data.name,
      initialCoinsAmount: data.initialCoinsAmount,
      participants: participants.filter(x => x.isEnabled)
    })
  }

  const rawSemester = watch('semester')
  const semester = useMemo(() => {
    return semesterPattern.test(rawSemester) ? rawSemester : ''
  }, [rawSemester])

  return (
    <form onSubmit={handleSubmit(submit)}>
      <FormControl isInvalid={errors.name}>
        <FormLabel htmlFor="name">Room name</FormLabel>
        <Input
          id="name"
          type="text"
          autoComplete="off"
          {...register('name', {
            required: 'This is required',
            minLength: { value: 1, message: 'Minimum length should be 1' }
          })}
        />
        <FormErrorMessage>
          {errors.name && errors.name.message}
        </FormErrorMessage>
      </FormControl>

      <Course onCourseIdChange={setCourseId} />

      <FormControl isInvalid={errors.semester} mt={8}>
        <FormLabel htmlFor="semester">Semester</FormLabel>
        <Input
          id="semester"
          type="text"
          autoComplete="off"
          placeholder="e.x. 2022L"
          {...register('semester', {
            required: 'This is required',
            pattern: semesterPattern
          })}
        />
        <FormErrorMessage>Incorrect format. E.x. 2022L</FormErrorMessage>
      </FormControl>

      <Participants
        courseId={courseId}
        semester={semester}
        onParticipantsChange={setParticipants}
      />

      <FormControl mt={8} isInvalid={errors.initialCoinsAmount}>
        <FormLabel htmlFor="initialCoinsAmount">Initial coins amount</FormLabel>
        <NumberInput defaultValue={100}>
          <NumberInputField
            id="initialCoinsAmount"
            {...register('initialCoinsAmount', {
              min: 0,
              required: true,
              setValueAs: parseInt
            })}
          />
        </NumberInput>
        <FormErrorMessage>
          {errors.initialCoinsAmount?.message}
        </FormErrorMessage>
      </FormControl>

      <Flex justifyContent="end">
        <Button
          mt={4}
          colorScheme="teal"
          isLoading={isSubmitting}
          type="submit"
        >
          Submit
        </Button>
      </Flex>
    </form>
  )
}

export default RoomForm
