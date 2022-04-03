package pw.coins.room

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.MembersDao
import pw.coins.db.generated.tables.daos.RoomsDao
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Room

@Service
class RoomService(
    private val roomsDao: RoomsDao,
    private val membersDao: MembersDao,
) {
    fun create(newRoom: NewRoom): Room {
        val room = Room().apply { name = newRoom.name }
        roomsDao.insert(room)
        return room
    }

    fun addMember(roomId: Long, newMember: NewMember): Member {
        val member = newMember.toMember(roomId)

        membersDao.insert(member)

        if (member.id == null) {
            throw Exception("Couldn't create member for user with an id = ${newMember.associatedUserId}, returned Id is null")
        }

        return member
    }

    fun getMembers(roomId: Long): List<Member> {
        return membersDao.fetchByRoomId(roomId)
    }

    fun removeMemberById(memberId: Long) {
        membersDao.deleteById(memberId)
    }
}

private fun NewMember.toMember(roomId: Long): Member {
    val x = Member()
    x.roomId = roomId
    x.userId = associatedUserId
    return x
}
