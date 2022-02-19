package pw.coins.task.dtos

import java.time.LocalDate

data class NewTask(
    val title: String,
    val content: String?,
    val deadline: LocalDate,
    val budget: Int,
    val roomId: Long,
)