import auth from '../security/auth'

/**
 * Create a new task
 * @param {ApiNewTask} task
 * @return {Promise<ApiTask>}
 */
export async function createTask(task) {
  return auth
    .getClient()
    .post(`/tasks`, task)
    .then(r => r.data)
}

/**
 * Assign a task to the given assigneeMemberId
 * @param {string} taskId
 * @param {string} assigneeMemberId
 * @return {Promise<ApiTask>}
 */
export async function assignTask(taskId, assigneeMemberId) {
  return auth
    .getClient()
    .post(`/tasks/${taskId}/assign`, { assigneeMemberId })
    .then(r => r.data)
}

/**
 * Assign a task to the given assigneeMemberId
 * @param {string} taskId
 * @return {Promise<ApiTask>}
 */
export async function unassignTask(taskId) {
  return auth
    .getClient()
    .post(`/tasks/${taskId}/unassign`)
    .then(r => r.data)
}

/**
 * Delete task
 * @param {string} taskId
 * @return Promise<AxiosResponse>
 */
export async function deleteTask(taskId) {
  return auth
    .getClient()
    .delete(`/tasks/${taskId}`)
    .then(r => r.data)
}
