package pw.coins.room

import org.springframework.stereotype.Service
import pw.coins.db.generated.public_.tables.daos.MembersDao
import pw.coins.db.generated.public_.tables.daos.RoomsDao
import pw.coins.db.generated.public_.tables.pojos.Member
import pw.coins.db.generated.public_.tables.pojos.Room
import pw.coins.room.dtos.NewRoom
import pw.coins.room.member.dtos.NewMember

@Service
class RoomSe(
    private val roomsDao: RoomsDao,
    private val membersDao: MembersDao,
) {
    fun create(newRoom: NewRoom): Room {
        val room = Room().apply { name = newRoom.name }
        roomsDao.insert(room)
        return room
    }

    fun addMember(roomId_: Long, newMember: NewMember): Member {
        val member = Member().apply {
            roomId = roomId_
            userId = newMember.associatedUserId
        }

        membersDao.insert(member)

        assert(member.id != null) {
            "Couldn't create member for user with an id = ${newMember.associatedUserId}, returned Id is null"
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