package pw.coins.task.dtos

/**
 * All the possible task status values
 */
@Suppress("unused")
enum class TaskStatus(value: String) {
    NEW("New"), IN_PROGRESS("In progress"), FINISHED("Finished");

    val formatted = value
}