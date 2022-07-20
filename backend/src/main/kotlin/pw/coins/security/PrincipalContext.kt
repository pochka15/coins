package pw.coins.security

import org.springframework.security.core.annotation.AuthenticationPrincipal

@AuthenticationPrincipal(expression = "systemUser")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PrincipalContext
