package pw.coins.room.model

import pw.coins.db.generated.tables.pojos.Member
import java.util.*

data class UserWithMember(
    val id: UUID,
    val isEnabled: Boolean,
    val name: String,
    val email: String,
    val member: Member,
)
