package pw.coins.task

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import pw.coins.db.generated.Tables
import pw.coins.room.NewRoom
import pw.coins.room.RoomService
import pw.coins.security.UuidSource
import pw.coins.user.UserService
import java.time.OffsetDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TaskControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val roomService: RoomService,
    @Autowired val userService: UserService,
    @Autowired val dslContext: DSLContext,
    @Autowired val uuidSource: UuidSource,
) {

    @Test
    fun `create task EXPECT correct dto returned`() {
        val room = roomService.create(NewRoom("Test room"))
        val user = userService.createUser("Test user")
        mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = /* language=JSON */ """
                    {
                      "title": "Test task",
                      "content": "Test content",
                      "deadline": "${OffsetDateTime.now().toLocalDate()}",
                      "budget": 10,
                      "roomId": "${room.id}",
                      "userId": "${user.id}"
                    }
                """.trimIndent()
        }.andExpect {
            content {
                jsonPath("$.id", notNullValue())
                jsonPath("$.title", equalTo("Test task"))
                jsonPath("$.content", equalTo("Test content"))
                jsonPath("$.deadline", notNullValue())
                jsonPath("$.budget", equalTo(10))
                jsonPath("$.status", equalTo("New"))
            }
        }
    }

    @Test
    fun `get task by non-existing id EXPECT bad request`() {
        mockMvc.get("/tasks/${uuidSource.genUuid()}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }
    }


    @AfterEach
    fun cleanup() {
        dslContext
            .deleteFrom(Tables.MEMBERS)
            .execute()

        dslContext
            .deleteFrom(Tables.TASKS)
            .execute()

        dslContext
            .deleteFrom(Tables.ROOMS)
            .execute()

        dslContext
            .deleteFrom(Tables.USERS)
            .execute()
    }
}