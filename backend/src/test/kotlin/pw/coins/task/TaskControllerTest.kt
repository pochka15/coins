package pw.coins.task

import org.hamcrest.CoreMatchers.*
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
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.db.generated.tables.pojos.User
import pw.coins.room.NewMember
import pw.coins.room.NewRoom
import pw.coins.room.RoomService
import pw.coins.security.UuidSource
import pw.coins.security.WithMockCustomUser
import pw.coins.user.UserService
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockCustomUser
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
        val user = userService.getUser("test-email@gmail.com")!!
        val member = roomService.addMember(NewMember(user.id.toString(), room.id.toString()))
        postTask(room.id.toString(), LocalDate.of(2024, 1, 1))
            .andExpect {
                content {
                    jsonPath("$.id", notNullValue())
                    jsonPath("$.title", equalTo("Test task"))
                    jsonPath("$.content", equalTo("Test content"))
                    jsonPath("$.deadline", equalTo("2024-01-01"))
                    jsonPath("$.creationDate", containsString(LocalDate.now().toString()))
                    jsonPath("$.budget", equalTo(10))
                    jsonPath("$.status", equalTo("New"))
                    jsonPath("$.author", equalTo("Test user"))
                    jsonPath("$.authorMemberId", equalTo(member.id.toString()))
                    jsonPath("$.assignee", nullValue())
                    jsonPath("$.assigneeMemberId", nullValue())
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
    fun `get task of another member EXPECT success`() {
        val room = roomService.create(NewRoom("Test room"))
        val user = userService.getUser("test-email@gmail.com")!!
        val anotherUser = userService.createUser("Test user")
        roomService.addMember(NewMember(user.id.toString(), room.id.toString()))
        roomService.addMember(NewMember(anotherUser.id.toString(), room.id.toString()))
        val task = taskService.create(
            buildNewTask(room, anotherUser)
        )
        mockMvc.get("/tasks/${task.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { is2xxSuccessful() } }
    }


    @Test
    fun `get task EXPECT correct author and assignee given`() {
        val room = roomService.create(NewRoom("Test room"))
        val user = userService.getUser("test-email@gmail.com")!!
        roomService.addMember(NewMember(user.id.toString(), room.id.toString()))
        val task = taskService.create(buildNewTask(room, user))
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
        postTask(room.id.toString(), LocalDate.now().minusDays(1))
            .andExpect {
                status { isBadRequest() }
                content {
                    jsonPath("$.errors.length()", equalTo(1))
                }
            }

    }

    @Test
    fun `create two tasks and expect tasks are sorted by creation date descending`() {
        val room = roomService.create(NewRoom("Test room"))
        val user = userService.createUser("Test user")
        roomService.addMember(NewMember(user.id.toString(), room.id.toString()))
        taskService.create(
            NewTask(
                title = "Task 1",
                content = "Test content",
                deadline = LocalDate.now(),
                budget = 10,
                roomId = room.id.toString(),
                userId = user.id.toString(),
            )
        )
        taskService.create(
            NewTask(
                title = "Task 2",
                content = "Test content",
                deadline = LocalDate.now(),
                budget = 10,
                roomId = room.id.toString(),
                userId = user.id.toString(),
            )
        )
        mockMvc.get("/rooms/${room.id}/tasks") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            content {
                jsonPath("$[0].title", equalTo("Task 2"))
                jsonPath("$[1].title", equalTo("Task 1"))
            }
        }
    }

    @Test
    fun `get task task from another room then EXPECT forbidden`() {
        val room = roomService.create(NewRoom("Test room"))
        val user = userService.createUser("Test user")
        roomService.addMember(NewMember(user.id.toString(), room.id.toString()))
        val task = taskService.create(
            buildNewTask(room, user)
        )
        mockMvc.get("/tasks/${task.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isForbidden() } }
    }

    @Test
    fun `create two members then assign task EXPECT task has correct status and assignee`() {
        val room = roomService.create(NewRoom("Test room"))
        val user = userService.getUser("test-email@gmail.com")!!
        val anotherUser = userService.createUser("Test user")
        val member = roomService.addMember(NewMember(user.id.toString(), room.id.toString()))
        roomService.addMember(NewMember(anotherUser.id.toString(), room.id.toString()))
        val task = taskService.create(buildNewTask(room, anotherUser))
        mockMvc.post("/tasks/${task.id}/assignee") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = /* language=JSON */ """
                            {
                              "assigneeMemberId": "${member.id}"
                            }
                        """.trimIndent()
        }.andExpect {
            content {
                jsonPath("$.status", equalTo("Assigned"))
                jsonPath("$.assigneeMemberId", equalTo(member.id.toString()))
            }
        }
    }

    private fun buildNewTask(
        room: Room,
        anotherUser: User
    ) = NewTask(
        title = "Test task",
        content = "Test content",
        deadline = LocalDate.of(2022, 7, 15),
        budget = 10,
        roomId = room.id.toString(),
        userId = anotherUser.id.toString(),
    )


    fun postTask(roomId: String, deadline: LocalDate = LocalDate.now()): ResultActionsDsl {
        return mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = /* language=JSON */ """
                            {
                              "title": "Test task",
                              "content": "Test content",
                              "deadline": "$deadline",
                              "budget": 10,
                              "roomId": "$roomId"
                            }
                        """.trimIndent()
        }
    }

    @AfterEach
    fun cleanup() {
        with(dslContext) {
            deleteFrom(Tables.TASKS).execute()
            deleteFrom(Tables.MEMBERS).execute()
            deleteFrom(Tables.ROOMS).execute()
            deleteFrom(Tables.USERS).execute()
        }
    }
}