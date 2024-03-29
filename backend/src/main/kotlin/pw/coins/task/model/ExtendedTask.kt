package pw.coins.task.model

import pw.coins.db.generated.tables.pojos.Task
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

data class ExtendedTask(
    val id: UUID,
    val title: String,
    val content: String,
    val deadline: LocalDate?,
    val creationDate: OffsetDateTime,
    val budget: Int,
    val status: TaskStatus,
    val roomId: UUID,
    val authorMemberId: UUID,
    val assigneeMemberId: UUID?,
    val authorName: String,
    val assigneeName: String?,
    val solutionNote: String?,
)

fun Task.toExtended(authorName: String, assigneeName: String? = null): ExtendedTask {
    return ExtendedTask(
        id = id,
        title = title,
        content = content,
        deadline = deadline,
        creationDate = creationDate,
        budget = budget,
        status = TaskStatus.valueOf(status),
        roomId = roomId,
        authorMemberId = authorMemberId,
        assigneeMemberId = assigneeMemberId,
        authorName = authorName,
        assigneeName = assigneeName,
        solutionNote = solutionNote,
    )
}