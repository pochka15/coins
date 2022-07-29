package pw.coins.room

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.room.model.MembersDao
import pw.coins.room.model.RoomsDao
import pw.coins.room.model.UserWithMember
import pw.coins.security.UuidSource
import java.sql.SQLException
import java.util.*

const val GLOBAL_ROOM_ID = "a6041b05-ebb9-4ff0-9b6b-d915d573afb2"

@Service
class RoomService(
    private val roomsDao: RoomsDao,
    private val membersDao: MembersDao,
    private val uuidSource: UuidSource,
) {
    fun create(newRoom: NewRoom): Room {
        val room = Room(uuidSource.genUuid(), newRoom.name)
        roomsDao.insert(room)
        return room
    }

    fun addMember(newMember: NewMember): Member = addMembers(listOf(newMember)).single()

    fun addMembers(members: List<NewMember>): List<Member> {
        val memberModels = members.map { Member(uuidSource.genUuid(), it.associatedUserId, it.roomId) }
        try {
            membersDao.insert(memberModels)
        } catch (e: Exception) {
            if (e.message?.contains("violates foreign key") == true) {
                throw SQLException("Couldn't add a new member. Incorrect associated user id or room id given")
            } else throw RuntimeException("Couldn't add a new member")
        }
        return memberModels
    }

    fun getMembersByRoom(roomId: UUID): MutableList<UserWithMember> {
        return membersDao.fetchByRoomIdJoiningUser(roomId)
    }

    fun removeMemberById(memberId: UUID) {
        membersDao.deleteById(memberId)
    }

    fun getMemberByUserIdAndRoomId(userId: UUID, roomId: UUID): Member? {
        return membersDao.fetchByUserIdAndRoomId(userId, roomId)
    }

    fun getMemberById(memberId: UUID): Member? {
        return membersDao.fetchOneById(memberId)
    }

    fun getAvailableRooms(userId: UUID): List<Room> {
        return roomsDao.fetchUserRooms(userId)
    }
}

data class NewMember(val associatedUserId: UUID, val roomId: UUID)

data class NewRoom(var name: String)