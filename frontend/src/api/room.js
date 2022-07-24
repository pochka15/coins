import auth from '../security/auth'

/**
 * Get member in the room.
 * @param {string} roomId
 * @return {Promise<ApiMember>}
 */
export async function getMember(roomId) {
  return auth
    .getClient()
    .get(`/rooms/${roomId}/members/me`)
    .then(r => r.data)
}

/**
 * Get room tasks by room id
 * @param {string} roomId
 * @return {Promise<ApiTask[]>}
 */
export function getRoomTasks(roomId) {
  return auth
    .getClient()
    .get(`/rooms/${roomId}/tasks`)
    .then(r => r.data)
}
