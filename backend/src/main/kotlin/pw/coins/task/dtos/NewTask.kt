package pw.coins.task.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

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
    val teamsUserId: String
)