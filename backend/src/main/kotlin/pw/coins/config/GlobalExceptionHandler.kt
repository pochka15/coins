package pw.coins.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pw.coins.db.UUIDParseException


@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(UUIDParseException::class)
    fun handleExceptions(exception: UUIDParseException, webRequest: WebRequest): ResponseEntity<Any>? {
        return ResponseEntity(
            object {
                @Suppress("unused")
                val message = "Couldn't parse UUID"
            }, HttpStatus.BAD_REQUEST
        )
    }

}