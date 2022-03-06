package pw.coins.user

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.User
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size


@RestController
@RequestMapping("/user")
@Tag(name = "User")
class UserCo(private val userSe: UserSe) {

    @PostMapping
    fun createNewUser(@RequestBody @Valid payload: CreateUserPayload): User {
        return userSe.createUser(payload.userName)
    }

    @GetMapping("/{id}/members")
    fun findAssociatedMembers(@PathVariable id: Long): List<Member> {
        return userSe.findAssociatedMembers(id)
    }

    @DeleteMapping("/{id}")
    fun removeUser(@PathVariable id: Long) {
        userSe.removeUserById(id)
    }
}

data class CreateUserPayload(
    @field:NotEmpty(message = "Name should not be empty")
    @field:Size(max = 20, message = "Name length should be <= 20")
    val userName: String
)
