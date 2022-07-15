package pw.coins.task

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.TasksDao
import pw.coins.db.generated.tables.pojos.Task
import pw.coins.security.UuidSource
import java.time.LocalDate
import java.util.UUID

@Service
class TaskService(
    private val tasksDao: TasksDao,
    val uuidSource: UuidSource,
) {
    fun create(newTask: NewTask): TaskData {
        val task = Task(
            uuidSource.genUuid(),
            newTask.title,
            newTask.content,
            newTask.deadline,
            newTask.budget,
            TaskStatus.NEW.formatted,
            newTask.roomId,
            newTask.userId,
            null,
        )
        tasksDao.insert(task)

        if (task.id == null) {
            throw Exception("Couldn't create a new task with title ${newTask.title}, returned Id is null")
        }

        return task.toData()
    }

    fun getTask(taskId: String): Task? {
        return tasksDao.fetchOneById(UUID.fromString(taskId))
    }

    fun getTasksByRoom(roomId: String): List<Task> {
        return tasksDao.fetchByRoomId(UUID.fromString(roomId))
    }

    fun solveTask(taskId: String) {
        val task = tasksDao.fetchOneById(UUID.fromString(taskId))!!
        task.status = TaskStatus.FINISHED.formatted
        tasksDao.update(task)
    }

    fun getUserTasks(userId: String): List<Task> {
        return tasksDao.fetchByAuthorUserId(UUID.fromString(userId))
    }
}

data class NewTask(
    val title: String,
    val content: String?,
    val deadline: LocalDate,
    val budget: Int,
    val roomId: UUID,
    val userId: UUID,
)

/**
 * All the possible task status values
 */
@Suppress("unused")
enum class TaskStatus(value: String) {
    NEW("New"), IN_PROGRESS("In progress"), FINISHED("Finished");

    val formatted = value
}