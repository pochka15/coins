package pw.coins.task.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [TaskDeadlineValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskDeadline(
    val message: String = "Deadline can be set from today or further",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
