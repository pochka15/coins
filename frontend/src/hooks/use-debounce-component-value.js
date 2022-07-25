import { useEffect, useState } from 'react'

/**
 * see https://usehooks.com/useDebounce/ and https://codesandbox.io/s/ted8o?file=/src/useDebounce.js
 * @param value
 * @param delay
 * @returns debounced value
 */
export function useDebounce(value, delay) {
  const [debouncedValue, setDebouncedValue] = useState(value)

  useEffect(
    () => {
      const handler = setTimeout(() => setDebouncedValue(value), delay)
      return () => clearTimeout(handler)
    },
    [value, delay] // Only re-call effect if value or delay changes
  )

  return debouncedValue
}
