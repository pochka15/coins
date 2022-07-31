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

/**
 * Converter
 * @param {ApiValidationError[]} validationErrors
 * @return {string}
 */
export function toErrorMessage(validationErrors) {
  return validationErrors
    .map(error => `${error.fieldName}: ${error.message || 'incorrect'}`)
    .join('\n')
}
