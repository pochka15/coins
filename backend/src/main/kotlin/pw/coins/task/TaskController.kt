package pw.coins.task

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import pw.coins.db.generated.tables.pojos.Task
import java.time.LocalDate
import java.util.*

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
    fun postTask(@RequestBody task: NewTask): TaskData {
        return taskService.create(task).toData()
    }
}

data class TaskData(
    val id: UUID,
    val title: String,
    val content: String,
    val deadline: LocalDate,
    val budget: Int,
    val status: String,
)

fun Task.toData(): TaskData {
    return TaskData(
        id = id,
        title = title,
        content = content,
        deadline = deadline,
        budget = budget,
        status = status,
    )
}