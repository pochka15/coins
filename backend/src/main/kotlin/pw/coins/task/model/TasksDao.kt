package pw.coins.task.model

import org.jooq.Configuration
import org.jooq.Record
import org.jooq.SelectJoinStep
import org.springframework.stereotype.Component
import pw.coins.db.generated.Tables.USERS
import pw.coins.db.generated.Tables.TASKS
import java.util.*
import pw.coins.db.generated.tables.daos.TasksDao as OriginalDao

@Component
class TasksDao(
    val configuration: Configuration
) : OriginalDao(configuration) {
    val authors = USERS.`as`("authors")
    val assignees = USERS.`as`("assignees")

    fun fetchExtendedTaskByTaskId(taskId: UUID): ExtendedTask? {
        return selectJoinedTasks()
            .where(TASKS.ID.eq(taskId))
            .fetchOne { toExtendedTask(it) }
    }

    fun fetchExtendedTasksByAuthorId(authorId: UUID): MutableList<ExtendedTask> {
        return selectJoinedTasks()
            .where(TASKS.AUTHOR_USER_ID.eq(authorId))
            .fetch { toExtendedTask(it) }
    }

    fun fetchExtendedTasksByRoomId(roomId: UUID): MutableList<ExtendedTask> {
        return selectJoinedTasks()
            .where(TASKS.ROOM_ID.eq(roomId))
            .fetch { toExtendedTask(it) }
    }

    private fun selectJoinedTasks(): SelectJoinStep<Record> {
        return ctx()
            .select()
            .from(
                TASKS
                    .join(authors).on(TASKS.AUTHOR_USER_ID.eq(authors.ID))
                    .leftJoin(assignees).on(TASKS.ASSIGNEE_USER_ID.eq(assignees.ID))
            )
    }

    private fun toExtendedTask(it: Record): ExtendedTask {
        val task = it.into(TASKS)
        val author = it.into(authors)
        val assignee = it.into(assignees)
        return ExtendedTask(
            id = task.id,
            title = task.title,
            content = task.content,
            deadline = task.deadline,
            budget = task.budget,
            status = task.status,
            authorUserId = task.authorUserId,
            assigneeUserId = task.assigneeUserId,
            authorName = author.name,
            assigneeName = assignee.name,
        )
    }
}