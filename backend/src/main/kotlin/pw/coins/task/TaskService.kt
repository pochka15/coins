package pw.coins.task

import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import pw.coins.db.generated.Tables.TASKS
import pw.coins.db.generated.tables.daos.CoinsLocksDao
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Task
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.room.RoomService
import pw.coins.security.UuidSource
import pw.coins.task.model.ExtendedTask
import pw.coins.task.model.TaskStatus
import pw.coins.task.model.TasksDao
import pw.coins.task.model.toExtended
import pw.coins.user.UserService
import pw.coins.wallet.WalletNotFoundException
import pw.coins.wallet.WalletService
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*


@Service
class TaskService(
    val tasksDao: TasksDao,
    val locksDao: CoinsLocksDao,
    val uuidSource: UuidSource,
    val userService: UserService,
    val roomService: RoomService,
    val walletService: WalletService,
    val dsl: DSLContext,
) {
    fun create(newTask: NewTask): ExtendedTask {
        val member = roomService.getMemberByUserIdAndRoomId(newTask.userId, newTask.roomId)
            ?: throw MemberNotFoundException("You are not a member of the given room")

        val wallet = member.findWallet()
            ?: throw WalletNotFoundException("Couldn't find a wallet for the given task author")

        val task = Task(
            uuidSource.genUuid(),
            newTask.title,
            newTask.content,
            newTask.deadline,
            OffsetDateTime.now(),
            newTask.budget,
            TaskStatus.NEW.name,
            newTask.roomId,
            member.id,
            null,
            null
        )
        dsl.transaction { c: Configuration ->
            with(c.dsl()) { executeInsert(newRecord(TASKS, task)) }
            walletService.lockCoins(wallet, task.id, task.budget)
        }

        return task.toExtended(userService.getUserById(newTask.userId)!!.name)
    }

    fun getExtendedTask(taskId: UUID): ExtendedTask? {
        return tasksDao.fetchExtendedTaskByTaskId(taskId)
    }

    fun getExtendedTasksByRoom(roomId: UUID): List<ExtendedTask> {
        return tasksDao.fetchExtendedTasksByRoomId(roomId)
    }

    fun solveTask(taskId: UUID, requestedUserId: UUID): ExtendedTask {
        val task = findTask(taskId)
            ?: throw TaskNotFoundException("Couldn't find a task to solve")

        if (TaskStatus.valueOf(task.status) != TaskStatus.ASSIGNED) {
            throw TaskStatusException("Task hasn't been assigned yet")
        }

        val assignee = task.findAssignee()
            ?: throw MemberNotFoundException("Task assignee is not found")

        if (assignee.userId != requestedUserId) {
            throw PermissionsException("You are not permitted to solve this task. You are not the assignee")
        }

        task.status = TaskStatus.REVIEWING.name
        tasksDao.update(task)
        return getExtendedTask(taskId)!!
    }

    fun acceptTask(taskId: UUID, requestedUserId: UUID): ExtendedTask {
        val task = findTask(taskId)
            ?: throw TaskNotFoundException("Couldn't find a task that must be accepted")

        val author = task.findAuthor()!!
        if (author.userId != requestedUserId) {
            throw PermissionsException("You are not permitted to accept this task. You are not the author")
        }

        if (task.status != TaskStatus.REVIEWING.name) {
            throw TaskStatusException("Task must have a 'Reviewing' status in order to accept it")
        }

//        TODO think about transactional problems
        val assigneeWallet = walletService.getWalletByMemberId(task.assigneeMemberId)
            ?: throw WalletNotFoundException("Assignee doesn't have a wallet. Cannot accept the task")

        with(findLock(task)!!) {
            walletService.transferLockedCoins(this, assigneeWallet)
            walletService.deleteLockById(id)
        }
        task.status = TaskStatus.CLOSED.name
        tasksDao.update(task)
        return getExtendedTask(taskId)!!
    }

    fun rejectTask(taskId: UUID, requestedUserId: UUID): ExtendedTask {
        val task = findTask(taskId)
            ?: throw TaskNotFoundException("Couldn't find a task that must be accepted")

        val author = task.findAuthor()!!
        if (author.userId != requestedUserId) {
            throw PermissionsException("You are not permitted to reject this task. You are not the author")
        }

        if (task.status != TaskStatus.REVIEWING.name) {
            throw TaskStatusException("Task must have a 'Reviewing' status in order to reject it")
        }

        task.status = TaskStatus.ASSIGNED.name
        tasksDao.update(task)
        return getExtendedTask(taskId)!!
    }


    fun assign(taskId: UUID, assigneeMemberId: UUID, requestedUserId: UUID): ExtendedTask {
        val assignee = roomService.getMemberById(assigneeMemberId)
            ?: throw MemberNotFoundException("Given assignee is not found")

        val task = findTask(taskId)
            ?: throw TaskNotFoundException("Couldn't find the task with an id $taskId")

        val errorMessage = when (TaskStatus.valueOf(task.status)) {
            TaskStatus.ASSIGNED -> "You cannot reassign a task. It's already assigned"
            TaskStatus.REVIEWING -> "You cannot reassign a task. It's already being reviewed"
            TaskStatus.CLOSED -> "You cannot assign a task. The task has already been closed"
            TaskStatus.NEW -> null
        }
        if (errorMessage != null) throw TaskStatusException(errorMessage)

        if (assignee.userId != requestedUserId) {
            throw AssignmentException("You can assign task only to yourself")
        }

        if (task.roomId != assignee.roomId) {
            throw AssignmentException("You are not a member of the room where task is created")
        }

        task.status = TaskStatus.ASSIGNED.name
        task.assigneeMemberId = assigneeMemberId

        tasksDao.update(task)
        return getExtendedTask(taskId)!!
    }

    fun unassignTask(taskId: UUID, requestedUserId: UUID): ExtendedTask {
        val task = findTask(taskId)
            ?: throw TaskNotFoundException("Couldn't find the task with an id $taskId")

        val member = roomService.getMemberById(task.assigneeMemberId)!!
        if (member.userId != requestedUserId) {
            throw PermissionsException("You cannot unassign the task, you are not an assignee")
        }

        if (task.status != TaskStatus.ASSIGNED.name) {
            throw TaskStatusException("Task must have 'Assigned' status")
        }

        task.status = TaskStatus.NEW.name
        task.assigneeMemberId = null
        tasksDao.update(task)
        return getExtendedTask(taskId)!!
    }

    fun deleteTask(taskId: UUID, requestedUserId: UUID) {
        val task = findTask(taskId)
            ?: throw TaskNotFoundException("Couldn't find a task that must be deleted")

        val author = task.findAuthor()!!

        if (author.userId != requestedUserId) {
            throw PermissionsException("You cannot delete task, you are not an author")
        }

//        TODO think about transactional problems
        val lock = findLock(task)
        if (lock != null) {
            with(lock) {
                walletService.transferLockedCoins(this, author.findWallet()!!)
                walletService.deleteLockById(id)
            }
        }
        tasksDao.deleteById(task.id)
    }

    private fun findTask(taskId: UUID): Task? = tasksDao.fetchOneById(taskId)
    private fun Task.findAuthor(): Member? = roomService.getMemberById(authorMemberId)
    private fun Task.findAssignee(): Member? = roomService.getMemberById(assigneeMemberId)
    private fun Member.findWallet(): Wallet? = walletService.getWalletByMemberId(id)
    private fun findLock(task: Task) = locksDao.fetchByTaskId(task.id).getOrNull(0)
}

data class NewTask(
    val title: String,
    val content: String?,
    val deadline: LocalDate?,
    val budget: Int,
    val roomId: UUID,
    val userId: UUID,
)

class MemberNotFoundException(message: String?) : RuntimeException(message)
class TaskNotFoundException(message: String?) : RuntimeException(message)
class AssignmentException(message: String?) : RuntimeException(message)
class TaskStatusException(message: String?) : RuntimeException(message)
class PermissionsException(message: String?) : RuntimeException(message)