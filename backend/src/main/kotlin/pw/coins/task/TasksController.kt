package pw.coins.task

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import pw.coins.db.generated.tables.pojos.User
import pw.coins.room.RoomService
import pw.coins.security.PrincipalContext
import pw.coins.task.model.ExtendedTask
import pw.coins.task.validation.TaskDeadline
import pw.coins.wallet.NotEnoughCoinsException
import pw.coins.wallet.WalletNotFoundException
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank


@RestController
@RequestMapping("/tasks")
@Tag(name = "Task")
class TasksController(
    private val taskService: TaskService,
    private val roomService: RoomService,
) {

    @GetMapping("/{task_id}")
    fun getTask(
        @PathVariable("task_id") id: String,
        @PrincipalContext user: User
    ): TaskData? {
        val task = taskService.getTask(id)
            ?: throw ResponseStatusException(BAD_REQUEST, "Couldn't find task with id: $id")

        roomService.getMemberByUserIdAndRoomId(user.id.toString(), task.roomId.toString())
            ?: throw ResponseStatusException(FORBIDDEN, "You are not a member of the room where task has been created")

        return task.toData()
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
                user.id.toString(),
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

    @PostMapping("/{taskId}/assignee")
    fun assignTask(
        @PathVariable taskId: String,
        @RequestBody payload: AssignTaskPayload,
        @PrincipalContext user: User,
    ): TaskData {
        return try {
            taskService.assign(taskId, payload.assigneeMemberId, user.id.toString()).toData()
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
}

data class TaskData(
    val id: String,
    val title: String,
    val content: String,
    val deadline: String,
    val creationDate: String,
    val budget: Int,
    val status: String,
    val author: String,
    val authorMemberId: String,
    val assignee: String?,
    val assigneeMemberId: String?,
)

fun ExtendedTask.toData(): TaskData {
    return TaskData(
        id = id.toString(),
        title = title,
        content = content,
        deadline = deadline.toString(),
        creationDate = creationDate.toString(),
        budget = budget,
        status = status.formatted,
        author = authorName,
        authorMemberId = authorMemberId.toString(),
        assignee = assigneeName,
        assigneeMemberId = assigneeMemberId?.toString()
    )
}

data class NewTaskPayload(
    @field:NotBlank
    val title: String,
    val content: String?,
    @field:TaskDeadline
    val deadline: LocalDate,
    @field:Min(1) @field:Max(1000_000, message = "Too big budget")
    val budget: Int,
    val roomId: String,
)

data class AssignTaskPayload(val assigneeMemberId: String)