package pw.coins.task

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.coins.db.generated.tables.pojos.Task

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task")
class TaskCo(
    private val taskSe: TaskSe,
) {

    @GetMapping("/{task_id}")
    fun getTask(@PathVariable("task_id") id: Long): Task? {
        return taskSe.getTask(id)
    }
}