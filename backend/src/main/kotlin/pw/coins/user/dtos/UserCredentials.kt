package pw.coins.user.dtos

import pw.coins.validation.UsernameIsFree
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size


data class UserCredentials(
    @field:NotEmpty(message = "Name should not be empty")
    @field:Size(max = 20, message = "Name length should be <= 20")
    @field:UsernameIsFree
    var name: String,

    @field:NotEmpty(message = "Password should not be empty")
    @field:Size(max = 64, min = 1, message = "Password length should be: 1 <= length <= 64")
    var password: String,

    @field:NotEmpty(message = "Email should not be empty")
    @field:Email(message = "Email should be in correct form")
    var email: String,
)
