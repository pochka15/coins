package pw.coins.user.dtos

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class CreateUserPayload(
    @field:NotEmpty(message = "Name should not be empty")
    @field:Size(max = 20, message = "Name length should be <= 20")
    val userName: String
)
