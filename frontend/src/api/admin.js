import auth from '../security/auth'

/**
 * Create a new room
 * @param {ApiNewRoom} room
 * @return {Promise<ApiRoom>}
 */
export function createRoom(room) {
  return auth
    .getClient()
    .post('/admin/room', room)
    .then(r => r.data)
}
