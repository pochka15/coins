package pw.coins.task

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.pojos.Task
import pw.coins.db.parseUUID
import pw.coins.room.RoomService
import pw.coins.security.UuidSource
import pw.coins.task.model.ExtendedTask
import pw.coins.task.model.TaskStatus
import pw.coins.task.model.TasksDao
import pw.coins.task.model.toExtended
import pw.coins.user.UserService
import java.time.LocalDate
import java.time.OffsetDateTime


@Service
class TaskService(
    val tasksDao: TasksDao,
    val uuidSource: UuidSource,
    val userService: UserService,
    val roomService: RoomService,
) {
    fun create(newTask: NewTask): ExtendedTask {
        val member = kotlin.runCatching {
            roomService.getMemberByUserIdAndRoomId(newTask.userId, newTask.roomId)
        }.getOrNull() ?: throw MemberNotFoundException("You are not a member of the given room")

        val task = Task(
            uuidSource.genUuid(),
            newTask.title,
            newTask.content,
            newTask.deadline,
            OffsetDateTime.now(),
            newTask.budget,
            TaskStatus.NEW.name,
            parseUUID(newTask.roomId),
            member.id,
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

    fun getMemberTasks(userId: String): List<ExtendedTask> {
        return tasksDao.fetchExtendedTasksByAuthorId(parseUUID(userId))
    }

    fun assign(taskId: String, assigneeMemberId: String, requestedUserId: String): ExtendedTask {
        val member = roomService.getMemberById(assigneeMemberId)
            ?: throw MemberNotFoundException("Given assignee is not found")

        val extendedTask = getTask(taskId)
            ?: throw TaskNotFoundException("Couldn't find the task with an id $taskId")

        val message = when (extendedTask.status) {
            TaskStatus.ASSIGNED -> "You cannot reassign a task"
            TaskStatus.FINISHED -> "You cannot assign a task. The task has already been solved"
            TaskStatus.NEW -> null
        }
        if (message != null) throw TaskStatusException(message)

        if (member.userId != parseUUID(requestedUserId)) {
            throw AssignmentException("You can assign task only to yourself")
        }

        if (extendedTask.roomId != member.roomId) {
            throw AssignmentException("You are not a member of the room where task is created")
        }

        val task = extendedTask.run {
            Task(
                id,
                title,
                content,
                deadline,
                creationDate,
                budget,
                TaskStatus.ASSIGNED.name,
                roomId,
                authorMemberId,
                parseUUID(assigneeMemberId),
            )
        }

        tasksDao.update(task)
        return getTask(taskId)!!
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

class MemberNotFoundException(message: String?) : Exception(message)
class TaskNotFoundException(message: String?) : Exception(message)
class AssignmentException(message: String?) : Exception(message)
class TaskStatusException(message: String?) : Exception(message)