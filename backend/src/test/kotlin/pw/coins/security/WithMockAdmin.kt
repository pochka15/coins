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
@WithSecurityContext(factory = AdminFactory::class)
annotation class WithMockAdmin(
    val username: String = "Test user",
    val email: String = "test-email@gmail.com",
)

class AdminFactory(private val userService: UserService) :
    WithSecurityContextFactory<WithMockAdmin> {
    override fun createSecurityContext(withUser: WithMockAdmin): SecurityContext {
        val user = userService.createUser(withUser.username, withUser.email)
        val authorities = mutableListOf(SimpleGrantedAuthority("USER"), SimpleGrantedAuthority("ADMIN"))
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
