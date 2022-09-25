package pw.coins.task.model

import org.jooq.Configuration
import org.jooq.Record
import org.jooq.SelectJoinStep
import org.springframework.stereotype.Component
import pw.coins.db.generated.Tables.*
import java.util.*
import pw.coins.db.generated.tables.daos.TasksDao as OriginalDao

const val AUTHOR_NAME_COLUMN = "authorName"
const val ASSIGNEE_NAME_COLUMN = "assigneeName"

@Component
class TasksDao(
    val configuration: Configuration
) : OriginalDao(configuration) {
    val userAuthors = USERS.`as`("authors")
    val memberAuthors = MEMBERS.`as`("memberAuthors")
    val userAssignees = USERS.`as`("assignees")
    val memberAssignees = MEMBERS.`as`("memberAssignees")

    fun fetchExtendedTaskByTaskId(taskId: UUID): ExtendedTask? {
        return selectJoinedTasks()
            .where(TASKS.ID.eq(taskId))
            .fetchOne { toExtendedTask(it) }
    }

    fun fetchExtendedTasksByAuthorId(authorId: UUID): MutableList<ExtendedTask> {
        return selectJoinedTasks()
            .where(TASKS.AUTHOR_MEMBER_ID.eq(authorId))
            .orderBy(TASKS.CREATION_DATE.desc())
            .fetch { toExtendedTask(it) }
    }

    fun fetchExtendedTasksByRoomId(roomId: UUID): MutableList<ExtendedTask> {
        return selectJoinedTasks()
            .where(TASKS.ROOM_ID.eq(roomId))
            .orderBy(TASKS.CREATION_DATE.desc())
            .fetch { toExtendedTask(it) }
    }

    private fun selectJoinedTasks(): SelectJoinStep<Record> {
        return ctx()
            .select(
                mutableListOf(
                    userAuthors.NAME.`as`(AUTHOR_NAME_COLUMN),
                    userAssignees.NAME.`as`(ASSIGNEE_NAME_COLUMN)
                ) + TASKS.fields()
            )
            .from(
                TASKS
//                    task -> member (author) -> user (author)
                    .join(memberAuthors).on(TASKS.AUTHOR_MEMBER_ID.eq(memberAuthors.ID))
                    .join(userAuthors).on(memberAuthors.USER_ID.eq(userAuthors.ID))
//                    task -> member (assignee) -> user (assignee)
                    .leftJoin(memberAssignees).on(TASKS.ASSIGNEE_MEMBER_ID.eq(memberAssignees.ID))
                    .leftJoin(userAssignees).on(memberAssignees.USER_ID.eq(userAssignees.ID))
            )
    }

    private fun toExtendedTask(it: Record): ExtendedTask {
        val task = it.into(TASKS)
        val authorName = it[AUTHOR_NAME_COLUMN] as String
        val assigneeName = it[ASSIGNEE_NAME_COLUMN] as String?
        return ExtendedTask(
            id = task.id,
            title = task.title,
            content = task.content,
            deadline = task.deadline,
            creationDate = task.creationDate,
            budget = task.budget,
            status = TaskStatus.valueOf(task.status),
            roomId = task.roomId,
            authorMemberId = task.authorMemberId,
            assigneeMemberId = task.assigneeMemberId,
            authorName = authorName,
            assigneeName = assigneeName,
            solutionNote = task.solutionNote,
        )
    }
}