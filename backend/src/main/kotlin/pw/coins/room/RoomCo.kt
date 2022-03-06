package pw.coins.room

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Room

@RestController
@RequestMapping("/room")
@Tag(name = "Room")
class RoomCo(val roomSe: RoomSe) {
    @PostMapping
    fun createRoom(@RequestBody room: NewRoom): Room {
        return roomSe.create(room)
    }

    @PostMapping("/{roomId}/members")
    fun addMember(@PathVariable roomId: Long, @RequestBody member: NewMember): Member {
        return roomSe.addMember(roomId, member)
    }

    @GetMapping("/{id}/members")
    fun roomMembers(@PathVariable id: Long): List<Member> {
        return roomSe.getMembers(id)
    }

    @DeleteMapping("/{id}/members/{memberId}")
    fun removeMember(@PathVariable id: Long, @PathVariable memberId: Long) {
        roomSe.removeMemberById(memberId)
    }
}

data class NewMember(
    val associatedUserId: Long
)

data class NewRoom(
    var name: String
)
