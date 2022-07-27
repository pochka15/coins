package pw.coins.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException
import javax.validation.ConstraintViolationException


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleConstraintValidationException(e: ConstraintViolationException): ValidationErrorResponse {
        val error = ValidationErrorResponse()
        for (violation in e.constraintViolations) {
            error.errors.add(FieldError(violation.propertyPath.toString(), violation.message))
        }
        return error
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ValidationErrorResponse {
        val error = ValidationErrorResponse()
        for (fieldError in e.bindingResult.fieldErrors) {
            error.errors.add(FieldError(fieldError.field, fieldError.defaultMessage!!))
        }
        return error
    }

    @ExceptionHandler(ResponseStatusException::class)
    @ResponseBody
    fun handleResponseStatusException(e: ResponseStatusException): ResponseEntity<ErrorMessageResponse> {
        return ResponseEntity(ErrorMessageResponse(e.reason ?: e.message), e.status)
    }
}

data class FieldError(val fieldName: String, val message: String)

class ValidationErrorResponse {
    var errors: MutableList<FieldError> = mutableListOf()
}

data class ErrorMessageResponse(val message: String)