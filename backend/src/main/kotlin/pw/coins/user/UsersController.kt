package pw.coins.user

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.db.generated.tables.pojos.User
import pw.coins.room.MemberData
import pw.coins.room.RoomData
import pw.coins.room.RoomService
import pw.coins.room.toData
import pw.coins.security.PrincipalContext
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size


@RestController
@RequestMapping("/users")
@Tag(name = "Users")
class UsersController(
    val userService: UserService,
    val roomService: RoomService
) {

    @PostMapping
    fun createNewUser(@RequestBody @Valid payload: CreateUserPayload): UserData {
        return userService.createUser(payload.userName).toData()
    }

    @GetMapping("/{id}/members")
    fun getAssociatedMembers(@PathVariable id: UUID): List<MemberData> {
        return userService.findAssociatedMembers(id).map { it.toData() }
    }

    @GetMapping("/availableRooms")
    fun getAvailableRooms(@PrincipalContext user: User): List<RoomData> {
        return roomService.getAvailableRooms(user.id).map { it.toData() }
    }

    @DeleteMapping("/{id}")
    fun removeUser(@PathVariable id: UUID) {
        userService.removeUserById(id)
    }
}

data class CreateUserPayload(
    @field:NotEmpty(message = "Name should not be empty")
    @field:Size(max = 20, message = "Name length should be <= 20")
    val userName: String
)

data class UserData(
    val email: String,
    val id: UUID,
    val name: String,
)

fun User.toData(): UserData {
    return UserData(email, id, name = name)
}