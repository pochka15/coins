import auth from '../auth'

/**
 * Get room tasks by room id
 * @param {number} roomId
 * @return {Promise<Task[]>}
 */
export async function getRoomTasks(roomId) {
  const { data } = await auth.getClient().get(`/room/${roomId}/tasks`)
  return data
}
