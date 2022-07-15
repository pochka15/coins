package pw.coins.room

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.RoomsDao
import pw.coins.db.generated.tables.daos.UsersDao
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.db.parseUUID
import pw.coins.room.model.UserWithMember
import pw.coins.room.model.MembersDao
import pw.coins.security.UuidSource

@Service
class RoomService(
    private val roomsDao: RoomsDao,
    private val membersDao: MembersDao,
    private val uuidSource: UuidSource,
    private val usersDao: UsersDao,
) {
    fun create(newRoom: NewRoom): Room {
        val room = Room(uuidSource.genUuid(), newRoom.name)
        roomsDao.insert(room)
        return room
    }

    fun addMember(newMember: NewMember): Member {
        val member = Member(null, parseUUID(newMember.associatedUserId), parseUUID(newMember.roomId))
        usersDao.fetchOneById(parseUUID(newMember.associatedUserId))
            ?: throw Exception("Cannot create a member with non-existing associated user id = ${newMember.associatedUserId}")

        membersDao.insert(member)

        if (member.id == null) {
            throw Exception("Couldn't create member for user with an id = ${newMember.associatedUserId}")
        }

        return member
    }

    fun getMembersByRoom(roomId: String): MutableList<UserWithMember> {
        return membersDao.fetchByRoomIdJoiningUser(parseUUID(roomId))
    }

    fun removeMemberById(memberId: Long) {
        membersDao.deleteById(memberId)
    }
}

data class NewMember(val associatedUserId: String, val roomId: String)

data class NewRoom(
    var name: String
)