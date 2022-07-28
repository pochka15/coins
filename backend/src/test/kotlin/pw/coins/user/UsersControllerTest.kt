package pw.coins.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.*
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import pw.coins.db.generated.Tables
import pw.coins.room.NewMember
import pw.coins.room.NewRoom
import pw.coins.room.RoomService
import pw.coins.security.WithMockCustomUser

@AutoConfigureMockMvc
@SpringBootTest
@WithMockCustomUser
internal class UsersControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mapper: ObjectMapper,
    @Autowired val dslContext: DSLContext,
    @Autowired val roomService: RoomService,
    @Autowired val userService: UserService,
) {

    @Test
    fun `create a user EXPECT correct dto returned`() {
        val payload = CreateUserPayload("tmp")
        val post = mockMvc.post("/users", arrayOf<Any?>()) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(payload)
        }
        post.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.id", notNullValue())
                jsonPath("$.name", `is`("tmp"))
            }
        }
    }

    @Test
    fun `add user to the rooms EXPECT correct available rooms returned`() {
        val me = me()
        val room1 = roomService.create(NewRoom("room1"))
        val room2 = roomService.create(NewRoom("room2"))
        roomService.addMember(NewMember(me.id, room1.id))
        roomService.addMember(NewMember(me.id, room2.id))

        mockMvc.get("/users/${me.id}/availableRooms") {
            accept = MediaType.APPLICATION_JSON
        }.andDo { print() }
    }


    @AfterEach
    fun cleanup() {
        with(dslContext) {
            deleteFrom(Tables.MEMBERS).execute()
            deleteFrom(Tables.ROOMS).execute()
            deleteFrom(Tables.USERS).execute()
        }
    }

    private fun me() = userService.getUserByEmail("test-email@gmail.com")!!
}