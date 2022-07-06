package pw.coins.bot

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pw.coins.bot.dtos.*
import pw.coins.task.TaskService
import pw.coins.task.dtos.TaskStatus
import pw.coins.user.wallet.dtos.Transaction
import pw.coins.user.UserService
import pw.coins.user.wallet.WalletService
import pw.coins.user.wallet.dtos.NewWallet
import java.time.LocalDate
import pw.coins.task.TaskData

@RestController
@RequestMapping("/bot")
@Tag(name = "Bot")
class BotController(
    val botService: BotService,
    val teamsUserService: TeamsUserService,
    val walletService: WalletService,
    val userService: UserService,
    val taskService: TaskService,
) {

    /**
     * Registration consists of multiple steps.
     * - create usual coins user
     * - create MS Teams user
     * - create a wallet for the user
     * - store the conversation between the bot and user
     */
    @PostMapping("register")
    fun registerBot(@RequestBody reference: ConversationReference) {
        val conversation = botService.getConversationById(reference.conversation.id)

//        Check if already initialized 
        if (conversation != null) return

        val user = userService.createUser(reference.user.name)

        teamsUserService.createTeamsUser(
            reference.user,
            null,
            user.id
        )

        walletService.createWallet(NewWallet("${reference.bot.name} wallet", 0, user.id))

        botService.createConversation(
            reference.conversation.id,
            user.id,
            jacksonObjectMapper().writeValueAsString(reference)
        )
    }

    @PostMapping("new-task")
    fun createTask(@RequestBody task: NewTask): TaskData {
        val teamsUser = botService.fetchTeamsUserById(task.teamsUserId)!!
        val author = userService.getUserById(teamsUser.originalUserId)!!

        return taskService.create(
            pw.coins.task.dtos.NewTask(
                task.title,
                task.content,
                task.deadline,
                task.budget,
                task.roomId,
                author.id,
            )
        )
    }


    /**
     * Solve the task with the given taskId. It's assumed that the task is solved by the assigned user.
     * There are made some pre-checks before running the main logic.
     * It's checked if the task is assigned to anyone and ensures that author and assignee have only one primary wallet
     */
    @Suppress("FoldInitializerAndIfToElvis")
    @PostMapping("tasks/{task_id}/solve")
    fun solveTask(
        @PathVariable("task_id") taskId: Long,
    ): ResponseEntity<String> {

        val task = taskService.getTask(taskId)

//        Check if task exists
        if (task == null) {
            return ResponseEntity
                .badRequest()
                .body("The task with an id $taskId doesn't exist")
        }

//        Check if task hasn't got status finished
        if (task.status == TaskStatus.FINISHED.formatted) {
            return ResponseEntity
                .badRequest()
                .body("The task ${task.title} has already been solved already solved by someone")
        }

//        Check if task is assigned
        if (task.assigneeUserId == null) {
            return ResponseEntity
                .badRequest()
                .body("The task '${task.title}' cannot be solved. It's not assigned to anyone")
        }

        val author = userService.getUserById(task.authorUserId)
        val assignee = userService.getUserById(task.assigneeUserId)!!

//        Ensure author has only one wallet
        val authorWallets = walletService.getUserWallets(author!!.id)
        if (authorWallets.size != 1) {
            return ResponseEntity
                .badRequest()
                .body("Task author has multiple wallets. Multiple wallets are not supported yet. Leave only one wallet")
        }

//        Ensure assignee has only one wallet
        val assigneeWallets = walletService.getUserWallets(assignee.id)
        if (assigneeWallets.size != 1) {
            return ResponseEntity
                .badRequest()
                .body("Task assignee has multiple wallets. Multiple wallets are not supported yet. Leave only one wallet")
        }

        walletService.executeTransaction(
            Transaction(
                authorWallets[0].id,
                assigneeWallets[0].id,
                task.budget
            )
        )
        taskService.solveTask(taskId)
        botService.notifyTaskSolved(task.title, author.id)
        return ResponseEntity.ok("Task ${task.title} has been successfully solved")
    }

    @PostMapping("home")
    fun getHomeData(@RequestBody payload: HomePayload): HomeData {
        val teamsUserId = payload.userId
        val userId = teamsUserService.getOriginalUserId(teamsUserId)
            ?: throw Exception("Couldn't find original user for the teams user.")

        val wallets = walletService.getUserWallets(userId)
        val tasks = taskService.getUserTasks(userId)
        return HomeData(
            wallets.map { Wallet(it.name, it.coinsAmount) },
            tasks.map { Task(it.title, it.status) }
        )
    }
}

/**
 * Income data that is used to create a new task
 */
data class NewTask(
    val title: String,
    val content: String?,
    val deadline: LocalDate,
    val budget: Int,
    val roomId: Long,
    @JsonProperty("userId")
    val teamsUserId: String,
)
