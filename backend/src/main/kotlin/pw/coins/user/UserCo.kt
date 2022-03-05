package pw.coins.user

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.User
import pw.coins.user.dtos.CreateUserPayload
import javax.validation.Valid


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