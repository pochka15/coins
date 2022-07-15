package pw.coins.room

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.user.UserService

@SpringBootTest
class RoomScenariosTest(
    @Autowired val roomService: RoomService,
    @Autowired val userService: UserService,
) {


//    @Test
//    fun `add members EXPECT correct amount of members returned`() {
//        val room = createRoom("room")
//        val membersAmount = 5
//        repeat(membersAmount) { roomService.addMember(room.id, NewMember(userService.createUser("tmp").id)) }
//        assertThat(roomService.getMembersByRoom(room.id).size).isEqualTo(membersAmount)
//    }
//
//    @Test
//    fun `remove member EXPECT no exceptions`() {
//        val room = createRoom("room")
//        val member = roomService.addMember(room.id, NewMember(userService.createUser("tmp").id))
//        roomService.removeMemberById(member.id)
//    }

    @Suppress("SameParameterValue")
    private fun createRoom(roomName: String = "tmp"): Room = roomService.create(NewRoom(roomName))

}