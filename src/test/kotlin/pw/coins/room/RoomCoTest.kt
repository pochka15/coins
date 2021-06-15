package pw.coins.room

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.emptyIterable
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import pw.coins.andExpectOkJson
import pw.coins.jsonGet
import pw.coins.jsonPost
import pw.coins.room.dtos.NewRoom
import pw.coins.room.dtos.Room
import pw.coins.room.member.Role
import pw.coins.room.member.dtos.Member
import pw.coins.room.member.dtos.NewMember
import pw.coins.user.UserSe
import pw.coins.user.dtos.User
import pw.coins.user.dtos.UserCredentials


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
internal class RoomCoTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mapper: ObjectMapper,
    @Autowired val roomSe: RoomSe,
    @Autowired val userSe: UserSe,
) {

    @Test
    @DirtiesContext
    fun `create room EXPECT correct dto`() {
        mockMvc.jsonPost(
            "/room"
        ) {
            content = mapper.writeValueAsString(NewRoom("test-room"))
        }.andExpect {
            content {
                jsonPath("$.name", `is`("test-room"))
                jsonPath("$.id", notNullValue())
            }
        }
    }

    @Test
    @DirtiesContext
    fun `add member EXPECT correct dto`() {
        mockMvc.jsonPost(
            "/room/${createRoom("room").id}/members"
        ) {
            content = mapper.writeValueAsBytes(NewMember(createUser().id))
        }.andExpectOkJson {
            content {
                jsonPath("$.id", notNullValue())
                jsonPath("$.roles", not(emptyIterable<Role>()))
            }
        }
    }

    @Test
    @DirtiesContext
    fun `add members EXPECT correct array`() {
        val room = createRoom("room")
        val membersAmount = 5
        repeat(membersAmount) { roomSe.addMember(room.id, NewMember(createUser("user${it}").id)) }

        mockMvc.jsonGet("/room/${room.id}/members") {
        }.andExpectOkJson {
            content {
                jsonPath("$", hasSize<Member>(membersAmount))
            }
        }
    }

    @Test
    @DirtiesContext
    fun `remove member EXPECT ok status`() {
        val room = createRoom("room")
        val member = roomSe.addMember(room.id, NewMember(createUser().id))

        mockMvc.delete("/room/${room.id}/members/${member.id}")
            .andExpect {
                status { isOk() }
            }
    }

    private fun createRoom(roomName: String = "tmp"): Room = roomSe.create(NewRoom(roomName))

    private fun createUser(name: String = "tmp"): User =
        userSe.createUser(UserCredentials(name, "tmp", "tmp"))
}