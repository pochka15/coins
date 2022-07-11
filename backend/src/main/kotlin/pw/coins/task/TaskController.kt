package pw.coins.task

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.db.generated.tables.pojos.Task
import pw.coins.task.dtos.NewTask
import java.util.*

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task")
class TaskController(
    private val taskService: TaskService,
) {

    @GetMapping("/{task_id}")
    fun getTask(@PathVariable("task_id") id: String): Task? {
        return taskService.getTask(UUID.fromString(id))
    }

    @PostMapping
    fun postTask(@RequestBody task: NewTask): TaskData {
        return taskService.create(task)
    }
}