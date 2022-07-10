import auth from '../security/auth'

/**
 * Get room tasks by room id
 * @param {number} roomId
 * @return {Promise<Task[]>}
 */
export async function getRoomTasks(roomId) {
  const { data } = await auth.getClient().get(`/room/${roomId}/tasks`)
  return data
}


/**
 * Create a new task
 * @param {NewTask} task
 * @return {Promise<Task>}
 */
export async function createTask(task) {
  const { data } = await auth.getClient().post(`/tasks`, task)
  return data
}