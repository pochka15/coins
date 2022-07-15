package pw.coins.task

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
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
        return taskService.getTask(id)?.toData()
    }

    @PostMapping
    fun postTask(@RequestBody task: NewTask): TaskData {
        return taskService.create(task)
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