package pw.coins.task

import io.swagger.v3.oas.annotations.tags.Tag
import org.hibernate.validator.constraints.Length
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import pw.coins.db.generated.tables.pojos.User
import pw.coins.room.RoomService
import pw.coins.security.PrincipalContext
import pw.coins.task.model.ExtendedTask
import pw.coins.task.validation.TaskDeadline
import pw.coins.wallet.LockNotFoundException
import pw.coins.wallet.NotEnoughCoinsException
import pw.coins.wallet.WalletNotFoundException
import java.time.LocalDate
import java.util.*
import javax.naming.NoPermissionException
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks")
class TasksController(
    private val taskService: TaskService,
    private val roomService: RoomService,
) {

    @GetMapping("/{task_id}")
    fun getTask(
        @PathVariable("task_id") id: UUID,
        @PrincipalContext user: User
    ): TaskData? {
        val task = taskService.getExtendedTask(id)
            ?: throw ResponseStatusException(BAD_REQUEST, "Couldn't find task with id: $id")

        roomService.getMemberByUserIdAndRoomId(user.id, task.roomId)
            ?: throw ResponseStatusException(FORBIDDEN, "You are not a member of the room where task has been created")

        return task.toData()
    }

    @DeleteMapping("/{task_id}")
    fun deleteTask(
        @PathVariable("task_id") id: UUID,
        @PrincipalContext user: User
    ) {
        try {
            taskService.deleteTask(id, user.id)
        } catch (e: PermissionsException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        } catch (e: LockNotFoundException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        } catch (e: TaskNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        }
    }

    @PostMapping
    fun postTask(
        @RequestBody @Valid newTask: NewTaskPayload,
        @PrincipalContext user: User
    ): TaskData {
        val task = newTask.let {
            NewTask(
                it.title,
                it.content,
                it.deadline,
                it.budget,
                it.roomId,
                user.id,
            )
        }
        return try {
            taskService.create(task).toData()
        } catch (e: MemberNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        } catch (e: WalletNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        } catch (e: NotEnoughCoinsException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }

    @PostMapping("/{taskId}/unassign")
    fun unassignTask(
        @PathVariable taskId: UUID,
        @PrincipalContext user: User,
    ): TaskData {
        return try {
            taskService.unassignTask(taskId, user.id).toData()
        } catch (e: NoPermissionException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        } catch (e: TaskStatusException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }

    @PostMapping("/{taskId}/assign")
    fun assignTask(
        @PathVariable taskId: UUID,
        @RequestBody payload: AssignTaskPayload,
        @PrincipalContext user: User,
    ): TaskData {
        return try {
            taskService.assign(taskId, payload.assigneeMemberId, user.id).toData()
        } catch (e: MemberNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        } catch (e: TaskNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        } catch (e: TaskStatusException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        } catch (e: AssignmentException) {
            throw ResponseStatusException(FORBIDDEN, e.message)
        }
    }

    @PostMapping("/{taskId}/solve")
    fun solveTask(
        @PathVariable("taskId") taskId: UUID,
        @PrincipalContext user: User
    ): TaskData {
        return try {
            taskService.solveTask(taskId, user.id).toData()
        } catch (e: TaskNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        } catch (e: MemberNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        } catch (e: PermissionsException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        } catch (e: TaskStatusException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }

    @PostMapping("/{taskId}/accept")
    fun acceptTask(
        @PathVariable("taskId") taskId: UUID,
        @PrincipalContext user: User
    ): TaskData {
        return try {
            taskService.acceptTask(taskId, user.id).toData()
        } catch (e: TaskNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        } catch (e: WalletNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        } catch (e: PermissionsException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        } catch (e: TaskStatusException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }

    @PostMapping("/{taskId}/reject")
    fun rejectTask(
        @PathVariable("taskId") taskId: UUID,
        @PrincipalContext user: User
    ): TaskData {
        return try {
            taskService.rejectTask(taskId, user.id).toData()
        } catch (e: TaskNotFoundException) {
            throw ResponseStatusException(NOT_FOUND, e.message)
        } catch (e: PermissionsException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        } catch (e: TaskStatusException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }
}

data class TaskData(
    val id: UUID,
    val title: String,
    val content: String,
    val deadline: String,
    val creationDate: String,
    val budget: Int,
    val status: String,
    val author: String,
    val authorMemberId: UUID,
    val assignee: String?,
    val assigneeMemberId: UUID?,
)

fun ExtendedTask.toData(): TaskData {
    return TaskData(
        id = id,
        title = title,
        content = content,
        deadline = deadline.toString(),
        creationDate = creationDate.toString(),
        budget = budget,
        status = status.formatted,
        author = authorName,
        authorMemberId = authorMemberId,
        assignee = assigneeName,
        assigneeMemberId = assigneeMemberId
    )
}

data class NewTaskPayload(
    @field:NotBlank
    val title: String,
    @field:Length(max = 1000, message = "Maximum 1000 characters is allowed in the content")
    val content: String?,
    @field:TaskDeadline
    val deadline: LocalDate,
    @field:Min(1) @field:Max(1000_000, message = "Too big budget")
    val budget: Int,
    val roomId: UUID,
)

data class AssignTaskPayload(val assigneeMemberId: UUID)