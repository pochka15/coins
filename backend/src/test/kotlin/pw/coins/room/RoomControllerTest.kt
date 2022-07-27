package pw.coins.room

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import pw.coins.db.generated.Tables.*
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.security.WithMockCustomUser
import pw.coins.user.UserService

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockCustomUser
internal class RoomControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val roomService: RoomService,
    @Autowired val dslContext: DSLContext,
    @Autowired val userService: UserService,
) {

    @Test
    fun `create room EXPECT correct name and id returned`() {
        mockMvc.post("/rooms") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = /* language=JSON */ """{"name": "test-room"}"""
        }.andExpect {
            content {
                jsonPath("$.name", equalTo("test-room"))
                jsonPath("$.id", notNullValue())
            }
        }
    }

    @Test
    fun `add member EXPECT member id is not null`() {
        val user = userService.createUser("test-user", "test@gmail.com")
        val room = createRoom("room")
        mockMvc.post("/rooms/${room.id}/members") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = /* language=JSON */ """{ "associatedUserId": "${user.id}" }"""
        }.andExpect {
            content {
                jsonPath("$.name", equalTo("test-user"))
                jsonPath("$.id", notNullValue())
            }
        }
    }

    @Test
    fun fetchByRoomIdJoiningUser() {
        val room = roomService.create(NewRoom("test-room"))
        val user = userService.createUser("test-user", "test@gmail.com")
        roomService.addMember(NewMember(user.id, room.id))
        mockMvc.get("/rooms/${room.id}/members") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            content {
                jsonPath("$.length()", equalTo(1))
                jsonPath("$[0].id", notNullValue())
                jsonPath("$[0].name", equalTo("test-user"))
            }
        }
    }

    fun createRoom(name: String): Room {
        return roomService.create(NewRoom(name))
    }

    @AfterEach
    fun cleanup() {
        dslContext
            .deleteFrom(MEMBERS)
            .execute()

        dslContext
            .deleteFrom(ROOMS)
            .execute()

        dslContext
            .deleteFrom(USERS)
            .execute()
    }
}