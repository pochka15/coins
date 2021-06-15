package pw.coins.user

import org.springframework.web.bind.annotation.*
import pw.coins.room.member.dtos.Member
import pw.coins.user.dtos.User
import pw.coins.user.dtos.UserCredentials
import javax.validation.Valid


@RestController
@RequestMapping("/user")
class UserCo(private val userSe: UserSe) {
    //        TODO(@pochka15): handle return validation error message if something went wrong
    @PostMapping
    fun createNewUser(@RequestBody @Valid userCredentials: UserCredentials): User {
        return userSe.createUser(userCredentials)
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