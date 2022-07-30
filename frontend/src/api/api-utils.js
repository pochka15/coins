export function extractErrorMessage(error) {
  return error.response.data?.message
}

/**
 * 
 * @param error
 * @return {ApiValidationError[]}
 */
export function extractValidationErrors(error) {
  return error.response.data?.errors
}

