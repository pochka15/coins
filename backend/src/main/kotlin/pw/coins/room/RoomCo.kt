package pw.coins.room

import org.springframework.web.bind.annotation.*
import pw.coins.room.dtos.NewRoom
import pw.coins.room.dtos.Room
import pw.coins.room.member.dtos.Member
import pw.coins.room.member.dtos.NewMember

@RestController
@RequestMapping("/room")
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