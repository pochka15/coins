package pw.coins.validation

import pw.coins.user.UserSe
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/**
 * Custom username validator that is used to create form constraints
 */
class UserNameValidator(private val userSe: UserSe) :
    ConstraintValidator<UsernameIsFree, String> {
    override fun isValid(username: String, context: ConstraintValidatorContext?): Boolean {
        return userSe.findByName(username) == null
    }
}