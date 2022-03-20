package pw.coins.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import pw.coins.db.generated.tables.daos.TeamsConversationsDao
import pw.coins.db.generated.tables.daos.TeamsUsersDao
import pw.coins.db.generated.tables.pojos.TeamsConversation
import pw.coins.db.generated.tables.pojos.TeamsUser
import java.time.Duration


enum class NotificationType {
    TASK_SOLVED
}

data class Notification(
    val type: NotificationType,
    val taskTitle: String,
    val rawConversationReference: String
)

/**
 * MS Teams bot service
 */
@Service
class BotSe(
    @Value("\${teamsbot.endpoint}")
    private val botEndpoint: String,
    private val conversationsDao: TeamsConversationsDao,
    private val teamsUsersDao: TeamsUsersDao,
) {

    /**
     * Notify bot that task has been solved
     * @param taskTitle - title of the task which is solved
     * @param targetUserId - id of the user which gets the notification
     */
    fun notifyTaskSolved(taskTitle: String, targetUserId: Long) {
        val rawConversationReference = getUserConversation(targetUserId)!!.rawConversationReference
        val body = Notification(NotificationType.TASK_SOLVED, taskTitle, rawConversationReference)

        WebClient.create(botEndpoint)
            .post()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .onStatus({ it != HttpStatus.OK }, ::toMonoException)
            .bodyToMono<Void>()
            .timeout(Duration.ofSeconds(5))
            .block()
    }

    fun fetchTeamsUserById(id: String): TeamsUser? = teamsUsersDao.fetchOneById(id)

    fun getConversationById(id: String): TeamsConversation? = conversationsDao.fetchOneById(id)

    fun getUserConversation(userId: Long): TeamsConversation? {
        val conversations = conversationsDao.fetchByUserId(userId)
        if (conversations.size > 1) {
            throw Exception("Multiple conversations found for the user with an Id: $userId")
        }

        return conversations.getOrNull(0)
    }

    fun createConversation(conversationId: String, userId: Long, rawConversationReference: String) {
        conversationsDao.insert(TeamsConversation(conversationId, rawConversationReference, userId))
    }

    private fun toMonoException(response: ClientResponse) =
        response.bodyToMono<String>().map { Exception(it) }
}