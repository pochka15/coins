package pw.coins.task

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.TasksDao
import pw.coins.db.generated.tables.pojos.Task
import pw.coins.task.dtos.NewTask
import pw.coins.task.dtos.TaskStatus

@Service
class TaskSe(private val tasksDao: TasksDao) {
    fun create(newTask: NewTask): Task {
        val task = Task(
            null,
            newTask.title,
            newTask.content,
            newTask.deadline,
            newTask.budget,
            TaskStatus.NEW.formatted,
            newTask.roomId,
            newTask.userId
        )
        tasksDao.insert(task)

        assert(task.id != null) {
            "Couldn't create a new task with title ${newTask.title}, returned Id is null"
        }

        return task
    }

    fun getTask(taskId: Long): Task? {
        return tasksDao.fetchOneById(taskId)
    }

    fun solveTask(taskId: Long) {
        val task = tasksDao.fetchOneById(taskId)
        task.status = TaskStatus.FINISHED.formatted
        tasksDao.update(task)
    }

    fun getUserTasks(userId: Long): List<Task> {
        return tasksDao.fetchByUserId(userId)
    }
}