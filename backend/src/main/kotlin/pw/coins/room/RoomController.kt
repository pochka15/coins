package pw.coins.room

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.room.model.UserWithMember
import pw.coins.task.TaskData
import pw.coins.task.TaskService
import pw.coins.task.toData
import pw.coins.user.UserService

@RestController
@RequestMapping("/room")
@Tag(name = "Room")
class RoomController(
    val roomService: RoomService,
    val taskService: TaskService,
    val userService: UserService,
) {
    @PostMapping
    fun createRoom(@RequestBody room: NewRoom): RoomData {
        return roomService.create(room).toData()
    }

    @PostMapping("/{roomId}/members")
    fun addMember(@PathVariable roomId: String, @RequestBody payload: NewMemberPayload): MemberData {
        val member = NewMember(payload.associatedUserId, roomId)
        val user = userService.getUserById(member.associatedUserId)!!
        return roomService.addMember(member).toData(user.name)
    }

    @GetMapping("/{id}/members")
    fun roomMembers(@PathVariable id: String): List<MemberData> {
        return roomService.getMembersByRoom(id).map { it.toData() }
    }

    @DeleteMapping("/{id}/members/{memberId}")
    fun removeMember(@PathVariable id: Long, @PathVariable memberId: Long) {
        roomService.removeMemberById(memberId)
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(@PathVariable id: String): List<TaskData> {
        return taskService.getTasksByRoom(id).map { it.toData() }
    }
}

data class NewMemberPayload(val associatedUserId: String)

data class RoomData(
    val id: String,
    val name: String
)

data class MemberData(
    val id: String,
    val name: String
)

fun Room.toData(): RoomData {
    return RoomData(id.toString(), name)
}

fun Member.toData(username: String): MemberData {
    return MemberData(id.toString(), username)
}

fun UserWithMember.toData(): MemberData {
    return MemberData(member.id.toString(), name)
}