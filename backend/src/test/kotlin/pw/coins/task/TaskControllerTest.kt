package pw.coins.task

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import pw.coins.db.generated.Tables
import pw.coins.room.NewRoom
import pw.coins.room.RoomService
import pw.coins.security.UuidSource
import pw.coins.user.UserService
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TaskControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val roomService: RoomService,
    @Autowired val userService: UserService,
    @Autowired val taskService: TaskService,
    @Autowired val dslContext: DSLContext,
    @Autowired val uuidSource: UuidSource,
) {

    @Test
    fun `create task EXPECT correct dto returned`() {
        val room = roomService.create(NewRoom("Test room"))
        val user = userService.createUser("Test user")
        postTask(room.id.toString(), user.id.toString())
            .andExpect {
                content {
                    jsonPath("$.id", notNullValue())
                    jsonPath("$.title", equalTo("Test task"))
                    jsonPath("$.content", equalTo("Test content"))
                    jsonPath("$.deadline", equalTo("2022-07-15"))
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

    @Test
    fun `get task EXPECT correct author and assignee given`() {
        val room = roomService.create(NewRoom("Test room"))
        val user = userService.createUser("Test user")
        val task = taskService.create(
            NewTask(
                title = "Test task",
                content = "Test content",
                deadline = LocalDate.of(2022, 7, 15),
                budget = 10,
                roomId = room.id.toString(),
                userId = user.id.toString(),
            )
        )
        mockMvc.get("/tasks/${task.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            content {
                jsonPath("$.author", equalTo("Test user"))
                jsonPath("$.assignee", nullValue())
            }
        }
    }

    @Test
    fun `create with deadline one day before today EXPECT bad request`() {
        val room = roomService.create(NewRoom("Test room"))
        val user = userService.createUser("Test user")
        postTask(room.id.toString(), user.id.toString(), LocalDate.now().minusDays(1))
            .andExpect {
                status { isBadRequest() }
                content {
                    jsonPath("$.errors.length()", equalTo(1))
                }
            }

    }

    fun postTask(roomId: String, userId: String, deadline: LocalDate = LocalDate.now()): ResultActionsDsl {
        return mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = /* language=JSON */ """
                            {
                              "title": "Test task",
                              "content": "Test content",
                              "deadline": "$deadline",
                              "budget": 10,
                              "roomId": "$roomId",
                              "userId": "$userId"
                            }
                        """.trimIndent()
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