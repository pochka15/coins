import React, { forwardRef } from 'react'
import ReactDatePicker from 'react-datepicker'
import {
  Input,
  InputGroup,
  InputRightElement,
  useColorMode
} from '@chakra-ui/react'
import { CalendarIcon } from '@chakra-ui/icons'

import 'react-datepicker/dist/react-datepicker.css'
import './chakra-react-datepicker.css'

const customDateInput = ({ value, onClick, onChange }, ref) => (
  <Input
    autoComplete="off"
    value={value}
    ref={ref}
    onClick={onClick}
    onChange={onChange}
  />
)
customDateInput.displayName = 'DateInput'

const CustomInput = forwardRef(customDateInput)

export function CustomDatePicker({ selectedDate, onChange, onBlur }) {
  const isLight = useColorMode().colorMode === 'light'

  return (
    <div className={isLight ? 'light-theme' : 'dark-theme'}>
      <InputGroup>
        <ReactDatePicker
          selected={selectedDate}
          onChange={onChange}
          onBlur={onBlur}
          showPopperArrow={true}
          className="react-datapicker__input-text"
          dateFormat="yyyy-MM-dd"
          customInput={<CustomInput />}
        />
        <InputRightElement
          color="gray.500"
          children={<CalendarIcon fontSize="sm" />}
        />
      </InputGroup>
    </div>
  )
}
