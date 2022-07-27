import usePersistentContext from './use-persistent-context'
import { useMemo } from 'react'

export const CURRENT_ROOM_KEY = 'currentRoom'

/**
 *
 * @return {ApiRoom | null}
 */
export function useCurrentRoom() {
  const [rawRoom] = usePersistentContext(CURRENT_ROOM_KEY)
  return useMemo(() => (rawRoom ? JSON.parse(rawRoom) : null), [rawRoom])
}
