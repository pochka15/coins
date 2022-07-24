package pw.coins.task.model

/**
 * All the possible task status values
 */
enum class TaskStatus(value: String) {
    NEW("New"),
    ASSIGNED("Assigned"),
    FINISHED("Finished");

    val formatted = value
}
