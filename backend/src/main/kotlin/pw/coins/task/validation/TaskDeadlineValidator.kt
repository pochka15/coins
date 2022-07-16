package pw.coins.task.validation

import java.time.LocalDate
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class TaskDeadlineValidator : ConstraintValidator<TaskDeadline, LocalDate> {
    override fun isValid(deadline: LocalDate, cxt: ConstraintValidatorContext): Boolean {
        return deadline.isAfter(LocalDate.now().minusDays(1))
    }
}
