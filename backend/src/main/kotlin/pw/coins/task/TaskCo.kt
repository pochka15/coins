package pw.coins.task

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.db.generated.tables.pojos.Task
import pw.coins.task.dtos.NewTask

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task")
class TaskCo(
    private val taskSe: TaskSe
) {

    @PostMapping("/new")
    fun createTask(@RequestBody task: NewTask): Task {
        return taskSe.create(task)
    }

    @GetMapping("/{task_id}")
    fun getTask(@PathVariable("task_id") id: Long): Task? {
        return taskSe.getTask(id)
    }
}