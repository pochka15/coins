package pw.coins.room

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.MembersDao
import pw.coins.db.generated.tables.daos.RoomsDao
import pw.coins.db.generated.tables.daos.UsersDao
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.security.UuidSource
import java.util.*

@Service
class RoomService(
    private val roomsDao: RoomsDao,
    private val membersDao: MembersDao,
    private val uuidSource: UuidSource,
    private val usersDao: UsersDao,
) {
    fun create(newRoom: NewRoom): RoomData {
        val room = Room(uuidSource.genUuid(), newRoom.name)
        roomsDao.insert(room)
        return room.toData()
    }

    fun addMember(newMember: NewMember): MemberData {
        val member = Member(null, newMember.associatedUserId, newMember.roomId)
        val user = usersDao.fetchOneById(newMember.associatedUserId)
            ?: throw Exception("Cannot create a member with non-existing associated user id = ${newMember.associatedUserId}")

        membersDao.insert(member)

        if (member.id == null) {
            throw Exception("Couldn't create member for user with an id = ${newMember.associatedUserId}")
        }

        return member.toData(user.name)
    }

    fun getMembersByRoom(roomId: UUID): List<MemberData> {
        val members = membersDao.fetchByRoomId(roomId)
        return members.map { it.toData("TODO") }
    }

    fun removeMemberById(memberId: Long) {
        membersDao.deleteById(memberId)
    }
}

data class RoomData(
    val id: String,
    val name: String
)

data class MemberData(
    val id: String,
    val name: String
)

private fun Room.toData(): RoomData {
    return RoomData(id.toString(), name)
}

private fun Member.toData(username: String): MemberData {
    return MemberData(id.toString(), username)
}
