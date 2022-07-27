import auth from '../security/auth'

/**
 * Get my wallet from the given room
 * @param {string} roomId
 * @return {Promise<ApiWallet>}
 */
export async function getWallet(roomId) {
  return auth
    .getClient()
    .get(`/wallets?roomId=${roomId}`)
    .then(r => r.data)
}
