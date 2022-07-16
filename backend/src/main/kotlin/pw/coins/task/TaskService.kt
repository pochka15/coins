package pw.coins.task

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.pojos.Task
import pw.coins.db.parseUUID
import pw.coins.security.UuidSource
import pw.coins.task.model.ExtendedTask
import pw.coins.task.model.TasksDao
import pw.coins.task.model.toExtended
import pw.coins.user.UserService
import java.time.LocalDate

@Service
class TaskService(
    val tasksDao: TasksDao,
    val uuidSource: UuidSource,
    val userService: UserService,
) {
    fun create(newTask: NewTask): ExtendedTask {
        val task = Task(
            uuidSource.genUuid(),
            newTask.title,
            newTask.content,
            newTask.deadline,
            newTask.budget,
            TaskStatus.NEW.formatted,
            parseUUID(newTask.roomId),
            parseUUID(newTask.userId),
            null,
        )
        tasksDao.insert(task)

        if (task.id == null) {
            throw Exception("Couldn't create a new task with title ${newTask.title}, returned Id is null")
        }

        return task.toExtended(userService.getUserById(newTask.userId)!!.name)
    }

    fun getTask(taskId: String): ExtendedTask? {
        return tasksDao.fetchExtendedTaskByTaskId(parseUUID(taskId))
    }

    fun getTasksByRoom(roomId: String): List<ExtendedTask> {
        return tasksDao.fetchExtendedTasksByRoomId(parseUUID(roomId))
    }

    fun solveTask(taskId: String) {
        val task = tasksDao.fetchOneById(parseUUID(taskId))!!
        task.status = TaskStatus.FINISHED.formatted
        tasksDao.update(task)
    }

    fun getUserTasks(userId: String): List<ExtendedTask> {
        return tasksDao.fetchExtendedTasksByAuthorId(parseUUID(userId))
    }
}

data class NewTask(
    val title: String,
    val content: String?,
    val deadline: LocalDate,
    val budget: Int,
    val roomId: String,
    val userId: String,
)

/**
 * All the possible task status values
 */
@Suppress("unused")
enum class TaskStatus(value: String) {
    NEW("New"), IN_PROGRESS("In progress"), FINISHED("Finished");

    val formatted = value
}