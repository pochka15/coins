package pw.coins.security.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.*
import pw.coins.db.generated.tables.pojos.User as SystemUser

/**
 * This class is primarily created to supply the SystemUser to the controller methods
 */
class CustomUserDetails(
    username: String,
    password: String,
    authorities: MutableCollection<out GrantedAuthority>,
    val id: UUID,
    val email: String
) : User(username, password, authorities) {
    @Suppress("unused")
    val systemUser = SystemUser(id, true, username, email)
}