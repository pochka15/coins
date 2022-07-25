export function extractErrorMessage(error) {
  return error.response.data?.message || ''
}
