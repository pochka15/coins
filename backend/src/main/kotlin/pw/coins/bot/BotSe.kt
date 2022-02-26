package pw.coins.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
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
    private val botEndpoint: String
) {

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

    private fun toMonoException(response: ClientResponse) =
        response.bodyToMono<String>().map { Exception(it) }
}