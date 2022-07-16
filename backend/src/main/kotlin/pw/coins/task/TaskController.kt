package pw.coins.task

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import pw.coins.task.model.ExtendedTask
import javax.validation.Valid

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task")
class TaskController(
    private val taskService: TaskService,
) {

    @GetMapping("/{task_id}")
    fun getTask(@PathVariable("task_id") id: String): TaskData? {
        val task = taskService.getTask(id)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't find task with id: $id")
        return task.toData()
    }

    @PostMapping
    fun postTask(@RequestBody @Valid task: NewTask): TaskData {
        return taskService.create(task).toData()
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
    val authorUserId: String,
    val assignee: String?,
    val assigneeUserId: String?,
)

fun ExtendedTask.toData(): TaskData {
    return TaskData(
        id = id.toString(),
        title = title,
        content = content,
        deadline = deadline.toString(),
        creationDate = creationDate.toString(),
        budget = budget,
        status = status,
        author = authorName,
        authorUserId = authorUserId.toString(),
        assignee = assigneeName,
        assigneeUserId = assigneeUserId?.toString()
    )
}