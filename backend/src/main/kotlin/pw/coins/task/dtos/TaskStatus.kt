package pw.coins.task.dtos

@Suppress("unused")
enum class TaskStatus(value: String) {
    NEW("New"), IN_PROGRESS("In progress"), FINISHED("Finished");

    val formatted = value
}