import auth from '../security/auth'

/**
 * Get room tasks by room id
 * @param {string} roomId
 * @return {Promise<Task[]>}
 */
export function getRoomTasks(roomId) {
  return auth
    .getClient()
    .get(`/room/${roomId}/tasks`)
    .then(r => r.data)
}

/**
 * Create a new task
 * @param {ApiNewTask} task
 * @return {Promise<Task>}
 */
export async function createTask(task) {
  return auth
    .getClient()
    .post(`/tasks`, task)
    .then(r => r.data)
}
