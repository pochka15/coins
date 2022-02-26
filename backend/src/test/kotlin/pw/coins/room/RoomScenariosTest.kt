package pw.coins.room

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.db.generated.tables.pojos.User
import pw.coins.room.dtos.NewRoom
import pw.coins.room.member.dtos.NewMember
import pw.coins.user.UserSe
import pw.coins.user.dtos.UserCredentials

@SpringBootTest
class RoomScenariosTest(
    @Autowired val roomSe: RoomSe,
    @Autowired val userSe: UserSe,
) {

    @Test
    fun `add member EXPECT member id is not null`() {
        val roomId = createRoom("room").id
        val member = roomSe.addMember(roomId, NewMember(createUser().id))
        assertThat(member.id).isNotNull
    }

    @Test
    fun `add members EXPECT correct array`() {
        val room = createRoom("room")
        val membersAmount = 5
        repeat(membersAmount) { roomSe.addMember(room.id, NewMember(createUser("user${it}").id)) }
        assertThat(roomSe.getMembers(room.id).size).isEqualTo(membersAmount)
    }

    @Test
    fun `remove member EXPECT no exceptions`() {
        val room = createRoom("room")
        val member = roomSe.addMember(room.id, NewMember(createUser().id))
        roomSe.removeMemberById(member.id)
    }

    @Suppress("SameParameterValue")
    private fun createRoom(roomName: String = "tmp"): Room = roomSe.create(NewRoom(roomName))

    private fun createUser(name: String = "tmp"): User =
        userSe.createUser(UserCredentials(name, "tmp", "tmp"))
}