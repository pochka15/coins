package pw.coins.room

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.db.generated.tables.pojos.User
import pw.coins.room.model.UserWithMember
import pw.coins.security.PrincipalContext
import pw.coins.task.TaskData
import pw.coins.task.TaskService
import pw.coins.task.toData
import pw.coins.user.UserService
import java.sql.SQLException
import java.util.*

@RestController
@RequestMapping("/rooms")
@Tag(name = "Rooms")
class RoomsController(
    val roomService: RoomService,
    val taskService: TaskService,
    val userService: UserService,
) {
    @PostMapping("/{roomId}/members")
    fun addMember(@PathVariable roomId: UUID, @RequestBody payload: NewMemberPayload): MemberData {
        return try {
            roomService.addMember(NewMember(payload.associatedUserId, roomId)).toData()
        } catch (e: SQLException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }

    @GetMapping("/{roomId}/members")
    fun roomMembers(@PathVariable roomId: UUID): List<MemberWithNameData> {
        return roomService.getMembersByRoom(roomId).map { it.toData() }
    }

    @GetMapping("/{roomId}/members/me")
    fun getMe(@PathVariable roomId: UUID, @PrincipalContext user: User): MemberData {
        val member = roomService.getMemberByUserIdAndRoomId(user.id, roomId)
            ?: throw ResponseStatusException(NOT_FOUND, "Couldn't find member")
        return MemberData(member.id, user.id, roomId)
    }

    @DeleteMapping("/{roomId}/members/{memberId}")
    fun removeMember(@PathVariable roomId: UUID, @PathVariable memberId: UUID) {
        roomService.removeMemberById(memberId)
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(@PathVariable id: UUID): List<TaskData> {
        return taskService.getExtendedTasksByRoom(id).map { it.toData() }
    }
}

data class NewMemberPayload(val associatedUserId: UUID)

data class RoomData(
    val id: UUID,
    val name: String
)

data class MemberData(
    val id: UUID,
    val userId: UUID,
    val roomId: UUID
)

data class MemberWithNameData(
    val id: UUID,
    val name: String
)

fun Room.toData(): RoomData {
    return RoomData(id, name)
}

fun UserWithMember.toData(): MemberWithNameData = MemberWithNameData(member.id, name)
fun Member.toData(): MemberData = MemberData(id, userId, roomId)