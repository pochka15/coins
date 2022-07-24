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
    .post(`/tasks/${taskId}/assignee`, { assigneeMemberId })
    .then(r => r.data)
}
