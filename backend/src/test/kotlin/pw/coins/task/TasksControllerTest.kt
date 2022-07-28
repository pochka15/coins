package pw.coins.task

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.emptyString
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.*
import pw.coins.db.generated.Tables
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.db.generated.tables.pojos.User
import pw.coins.room.NewMember
import pw.coins.room.NewRoom
import pw.coins.room.RoomService
import pw.coins.security.UuidSource
import pw.coins.security.WithMockCustomUser
import pw.coins.task.model.ExtendedTask
import pw.coins.user.UserService
import pw.coins.wallet.NewWallet
import pw.coins.wallet.WalletService
import java.time.LocalDate
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockCustomUser
class TasksControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val roomService: RoomService,
    @Autowired val userService: UserService,
    @Autowired val taskService: TaskService,
    @Autowired val walletService: WalletService,
    @Autowired val dslContext: DSLContext,
    @Autowired val uuidSource: UuidSource,
) {

    @Test
    fun `create task EXPECT correct dto returned`() {
        val member = with(room()) { member(me()).wallet(100) }
        postTask(member.roomId.toString(), LocalDate.of(2024, 1, 1))
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
        val task = with(room()) {
            member(me())
            member("Member")
                .wallet(100)
                .task(id)
        }

        mockMvc.get("/tasks/${task.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { is2xxSuccessful() } }
    }


    @Test
    fun `get task EXPECT correct author and assignee given`() {
        val task = with(room()) {
            member(me())
                .wallet(100)
                .task(id)
        }
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
        val room = with(room()) {
            member(me()).wallet(100)
            this
        }
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
        val room = with(room()) {
            val member = member(me())
                .wallet(100)
            member.task(id, "Task 1")
            member.task(id, "Task 2")
            this
        }
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
        val task = with(room()) {
            member("Member").wallet(100)
                .task(id, "Task 1")
        }
        mockMvc.get("/tasks/${task.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isForbidden() } }
    }

    @Test
    fun `create two members then assign task EXPECT task has correct status and assignee`() {
        val (member, task) = with(room()) {
            val mem = member(me())
            val task = member("Member")
                .wallet(10)
                .task(id)
            mem to task
        }

        postAssign(task.id, member.id).andExpect {
            content {
                jsonPath("$.status", equalTo("Assigned"))
                jsonPath("$.assigneeMemberId", equalTo(member.id.toString()))
            }
        }
    }

    @Test
    fun `create a task with not enough money EXPECT bad request and task is not stored into the db`() {
        val member = with(room()) {
            member(me()).wallet(0)
        }
        postTask(member.roomId.toString(), LocalDate.of(2024, 1, 1))
            .andExpect { status { isBadRequest() } }

        assertThat(taskService.getExtendedTasksByRoom(member.roomId))
            .hasSize(0)
    }

    @Test
    fun `create a task without a wallet EXPECT not found exception`() {
        val member = with(room()) {
            member(me())
        }
        postTask(member.roomId.toString(), LocalDate.of(2024, 1, 1))
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `delete task EXPECT coins are unlocked`() {
        val member = room().member(me())
        val wallet = walletService.createWallet(NewWallet(100, member.id))
        val task = member.task(member.roomId)

        mockMvc.delete("/tasks/${task.id}")
            .andExpect { status { is2xxSuccessful() } }

        getWallet(wallet.id.toString()).andExpect {
            jsonPath("$.coinsAmount", equalTo(100))
        }
    }

    @Test
    fun `assign and solve task EXPECT status is reviewing`() {
        val (me, task) = with(room()) {
            val me = member(me())
            me to member("Test")
                .wallet(100)
                .task(id)
        }
        postAssign(task.id, me.id)
        mockMvc.post("/tasks/${task.id}/solve") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            content {
                jsonPath("$.status", equalTo("Reviewing"))
                jsonPath("$.assigneeMemberId", equalTo(me.id.toString()))
            }
        }
    }


    @Test
    fun `solve task EXPECT bad request`() {
        val task = with(room()) {
            member(me())
            member("Test")
                .wallet(100)
                .task(id)
        }
        mockMvc.post("/tasks/${task.id}/solve") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `accept task EXPECT coins transaction is committed`() {
        val room = room()
        val me = room.member(me()).wallet(100)
        val him = room.member("Test").wallet(100)
        val task = me.task(room.id)
        taskService.assign(task.id, him.id, him.userId)
        taskService.solveTask(task.id, him.userId)

        mockMvc.post("/tasks/${task.id}/accept") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            content {
                jsonPath("$.status", equalTo("Closed"))
                jsonPath("$.assigneeMemberId", equalTo(him.id.toString()))
            }
        }

        val myWallet = walletService.getWalletByMemberId(me.id)!!
        val hisWallet = walletService.getWalletByMemberId(him.id)!!
        assertThat(myWallet.coinsAmount).isEqualTo(90)
        assertThat(hisWallet.coinsAmount).isEqualTo(110)
    }

    @Test
    fun `accept new task EXPECT bad request`() {
        val room = room()
        val me = room.member(me()).wallet(100)
        val task = me.task(room.id)

        mockMvc.post("/tasks/${task.id}/accept") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content { jsonPath("$.message", `is`(not(emptyString()))) }
        }
    }

    @Test
    fun `reject assigned task expect bad request`() {
        val room = room()
        val me = room.member(me()).wallet(100)
        val him = room.member("Him")
        val task = me.task(room.id).assign(him)

        mockMvc.post("/tasks/${task.id}/reject") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `reject task expect task now has status assigned`() {
        val room = room()
        val me = room.member(me()).wallet(100)
        val him = room.member("Him")
        val task = me.task(room.id).assign(him).solve(him)

        mockMvc.post("/tasks/${task.id}/reject") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            content {
                jsonPath("$.status", equalTo("Assigned"))
                jsonPath("$.assigneeMemberId", equalTo(him.id.toString()))
            }
        }
    }

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

    fun getWallet(walletId: String): ResultActionsDsl {
        return mockMvc.get("/wallets/${walletId}") { accept = MediaType.APPLICATION_JSON }
    }

    @AfterEach
    fun cleanup() {
        with(dslContext) {
            deleteFrom(Tables.COINS_LOCKS).execute()
            deleteFrom(Tables.WALLETS).execute()
            deleteFrom(Tables.TASKS).execute()
            deleteFrom(Tables.MEMBERS).execute()
            deleteFrom(Tables.ROOMS).execute()
            deleteFrom(Tables.USERS).execute()
        }
    }

    private fun room() = roomService.create(NewRoom("Test room"))

    private fun me() = userService.getUserByEmail("test-email@gmail.com")!!

    private fun Room.member(user: User): Member {
        return roomService.addMember(NewMember(user.id, id))
    }

    private fun Room.member(userName: String): Member {
        val user = userService.createUser(userName)
        return roomService.addMember(NewMember(user.id, id))
    }

    private fun Member.wallet(coinsAmount: Int): Member {
        return apply { walletService.createWallet(NewWallet(coinsAmount, id)) }
    }

    private fun ExtendedTask.assign(member: Member): ExtendedTask {
        return apply { taskService.assign(id, member.id, member.userId) }
    }

    private fun ExtendedTask.solve(member: Member): ExtendedTask {
        return apply { taskService.solveTask(id, member.userId) }
    }

    private fun Member.task(roomId: UUID, title: String = "Test task"): ExtendedTask {
        return taskService.create(
            NewTask(
                title = title,
                content = "Test content",
                deadline = LocalDate.of(2028, 7, 15),
                budget = 10,
                roomId = roomId,
                userId = userId,
            )
        )
    }

    private fun postAssign(
        taskId: UUID,
        assigneeMemberId: UUID
    ) = mockMvc.post("/tasks/$taskId/assign") {
        contentType = MediaType.APPLICATION_JSON
        accept = MediaType.APPLICATION_JSON
        content = /* language=JSON */ """
                                {
                                  "assigneeMemberId": "$assigneeMemberId"
                                }
                            """.trimIndent()
    }
}