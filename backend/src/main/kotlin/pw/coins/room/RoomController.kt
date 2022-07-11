package pw.coins.room

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.task.TaskData
import pw.coins.task.TaskService
import java.util.UUID

@RestController
@RequestMapping("/room")
@Tag(name = "Room")
class RoomController(
    val roomService: RoomService,
    val taskService: TaskService
) {
    @PostMapping
    fun createRoom(@RequestBody room: NewRoom): RoomData {
        return roomService.create(room)
    }

    @PostMapping("/{roomId}/members")
    fun addMember(@PathVariable roomId: String, @RequestBody associatedUserId: String): MemberData {
        return roomService.addMember(NewMember(UUID.fromString(roomId), UUID.fromString(roomId)))
    }

    @GetMapping("/{id}/members")
    fun roomMembers(@PathVariable id: String): List<MemberData> {
        return roomService.getMembersByRoom(UUID.fromString(id))
    }

    @DeleteMapping("/{id}/members/{memberId}")
    fun removeMember(@PathVariable id: Long, @PathVariable memberId: Long) {
        roomService.removeMemberById(memberId)
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(@PathVariable id: String): List<TaskData> {
        return taskService.getTasksByRoom(UUID.fromString(id))
    }
}

data class NewMember(val associatedUserId: UUID, val roomId: UUID)

data class NewRoom(
    var name: String
)
