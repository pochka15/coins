package pw.coins.task

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.TasksDao
import pw.coins.db.generated.tables.pojos.Task
import pw.coins.security.UuidSource
import pw.coins.task.dtos.NewTask
import pw.coins.task.dtos.TaskStatus
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

    fun getTask(taskId: UUID): Task? {
        return tasksDao.fetchOneById(taskId)
    }

    fun getTasksByRoom(roomId: UUID): List<TaskData> {
        val tasks = tasksDao.fetchByRoomId(roomId)
        return tasks.map { it.toData() }
    }

    fun solveTask(taskId: UUID) {
        val task = tasksDao.fetchOneById(taskId)!!
        task.status = TaskStatus.FINISHED.formatted
        tasksDao.update(task)
    }

    fun getUserTasks(userId: UUID): List<Task> {
        return tasksDao.fetchByAuthorUserId(userId)
    }
}

private fun Task.toData(): TaskData {
    return TaskData(
        id = id,
        title = title,
        content = content,
        deadline = deadline,
        budget = budget,
        status = status,
    )
}

data class TaskData(
    val id: UUID,
    val title: String,
    val content: String,
    val deadline: LocalDate,
    val budget: Int,
    val status: String,
)