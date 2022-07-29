package pw.coins.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import pw.coins.security.model.CustomUserDetails
import pw.coins.user.UserService

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = UserFactory::class)
annotation class WithMockCustomUser(
    val username: String = "Test user",
    val email: String = "test-email@gmail.com",
)

class UserFactory(private val userService: UserService) :
    WithSecurityContextFactory<WithMockCustomUser> {
    override fun createSecurityContext(withUser: WithMockCustomUser): SecurityContext {
        val user = userService.createUser(withUser.username, withUser.email)
        val authorities = mutableListOf(SimpleGrantedAuthority("USER"))
        val principal = CustomUserDetails(
            user.name,
            "",
            authorities,
            user.id,
            user.email
        )

        return SecurityContextHolder.createEmptyContext().also {
            it.authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)
        }
    }
}