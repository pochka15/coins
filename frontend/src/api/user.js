import auth from '../security/auth'

/**
 * Get available rooms
 * @return {Promise<ApiRoom>}
 */
export async function getAvailableRooms() {
  return auth
    .getClient()
    .get(`/users/availableRooms`)
    .then(r => r.data)
}

/**
 * Get my user
 * @return {Promise<ApiUser>}
 */
export async function getMe() {
  return auth
    .getClient()
    .get(`/users/me`)
    .then(r => r.data)
}
