package pw.coins.bot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.coins.bot.dtos.*
import pw.coins.task.TaskSe
import pw.coins.user.UserSe
import pw.coins.user.wallet.WalletSe
import pw.coins.user.wallet.dtos.NewWallet

@RestController
@RequestMapping("/bot")
@Tag(name = "Bot")
class BotCo(
    val botSe: BotSe,
    val teamsUserSe: TeamsUserSe,
    val walletSe: WalletSe,
    val userSe: UserSe,
    val taskSe: TaskSe,
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