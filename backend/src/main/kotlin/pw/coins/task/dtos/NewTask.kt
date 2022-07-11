package pw.coins.task.dtos

import java.time.LocalDate
import java.util.UUID

data class NewTask(
    val title: String,
    val content: String?,
    val deadline: LocalDate,
    val budget: Int,
    val roomId: UUID,
    val userId: UUID,
)
