package pw.coins.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import pw.coins.db.generated.tables.daos.TeamsConversationsDao
import pw.coins.db.generated.tables.pojos.TeamsConversation
import java.time.Duration


enum class NotificationType {
    TASK_SOLVED
}

data class Notification(
    val type: NotificationType,
    val taskId: Long
)

/**
 * MS Teams bot service
 */
@Service
class BotSe(
    @Value("\${teamsbot.endpoint}")
    private val botEndpoint: String,
    private val conversationsDao: TeamsConversationsDao
) {

    /**
     * Send the request to the bot that says that task with given ID has been solved
     */
    fun notifyTaskSolved(taskId: Long) {
        val body = Notification(NotificationType.TASK_SOLVED, taskId)

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

    fun getConversationById(id: String): TeamsConversation? {
        return conversationsDao.fetchOneById(id)
    }

    fun createConversation(conversationId: String, userId: Long, rawConversationReference: String) {
        conversationsDao.insert(TeamsConversation(conversationId, rawConversationReference, userId))
    }

    private fun toMonoException(response: ClientResponse) =
        response.bodyToMono<String>().map { Exception(it) }
}