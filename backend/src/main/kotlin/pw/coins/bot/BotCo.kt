package pw.coins.bot

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pw.coins.bot.dtos.*
import pw.coins.task.TaskSe
import pw.coins.transaction.TransactionSe
import pw.coins.transaction.dtos.Transaction
import pw.coins.user.UserSe
import pw.coins.user.wallet.WalletSe
import pw.coins.user.wallet.dtos.NewWallet
import java.time.LocalDate
import pw.coins.db.generated.tables.pojos.Task as TaskPojo

@RestController
@RequestMapping("/bot")
@Tag(name = "Bot")
class BotCo(
    val botSe: BotSe,
    val teamsUserSe: TeamsUserSe,
    val walletSe: WalletSe,
    val userSe: UserSe,
    val taskSe: TaskSe,
    val transactionSe: TransactionSe,
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
        val conversation = botSe.getConversationById(reference.conversation.id)

//        Check if already initialized 
        if (conversation != null) return

        val user = userSe.createUser(reference.user.name)

        teamsUserSe.createTeamsUser(
            reference.user,
            null,
            user.id
        )

        walletSe.createWallet(NewWallet("${reference.bot.name} wallet", 0, user.id))

        botSe.createConversation(
            reference.conversation.id,
            user.id,
            jacksonObjectMapper().writeValueAsString(reference)
        )
    }

    @PostMapping("new-task")
    fun createTask(@RequestBody task: NewTask): TaskPojo {
        val teamsUser = botSe.fetchTeamsUserById(task.teamsUserId)!!
        val author = userSe.getUserById(teamsUser.originalUserId)!!

        return taskSe.create(
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
     * There are made some pre-checks before running main logic.
     * It's checked if the task is assigned to anyone and ensures that author and assignee have only one primary wallet
     */
    @PostMapping("tasks/{task_id}/solve")
    fun solveTask(
        @PathVariable("task_id") taskId: Long,
    ): ResponseEntity<String> {

        val task = taskSe.getTask(taskId)
        val author = userSe.getUserById(task!!.authorUserId)
        val assignee = userSe.getUserById(task.assigneeUserId)

//        Check if the task is assigned
        @Suppress("FoldInitializerAndIfToElvis")
        if (assignee == null) {
            return ResponseEntity
                .badRequest()
                .body("The task ${task.title} cannot be solved. It's not assigned to anyone")
        }

//        Ensure users have only one wallet
        val authorWallets = walletSe.getUserWallets(author!!.id)
        if (authorWallets.size != 1) {
            return ResponseEntity
                .badRequest()
                .body("Task author has multiple wallets. Multiple wallets are not supported yet. Leave only one wallet")
        }
        val assigneeWallets = walletSe.getUserWallets(assignee.id)
        if (assigneeWallets.size != 1) {
            return ResponseEntity
                .badRequest()
                .body("Task assignee has multiple wallets. Multiple wallets are not supported yet. Leave only one wallet")
        }

        taskSe.solveTask(taskId)
        transactionSe.executeTransaction(
            Transaction(
                authorWallets[0].id,
                assigneeWallets[0].id,
                task.budget
            )
        )
        botSe.notifyTaskSolved(taskId, author.id)
        return ResponseEntity.ok("Task has been successfully solved")
    }

    @PostMapping("home")
    fun getHomeData(@RequestBody payload: HomePayload): HomeData {
        val teamsUserId = payload.userId
        val userId = teamsUserSe.getOriginalUserId(teamsUserId)
            ?: throw Exception("Couldn't find original user for the teams user.")

        val wallets = walletSe.getUserWallets(userId)
        val tasks = taskSe.getUserTasks(userId)
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
