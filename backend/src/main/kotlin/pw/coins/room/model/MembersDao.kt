package pw.coins.room.model

import org.jooq.Configuration
import org.springframework.stereotype.Component
import pw.coins.db.generated.Tables.MEMBERS
import pw.coins.db.generated.Tables.USERS
import pw.coins.db.generated.tables.pojos.Member
import java.util.*
import pw.coins.db.generated.tables.daos.MembersDao as OriginalDao

@Component
class MembersDao(
    val configuration: Configuration
) : OriginalDao(configuration) {

    fun fetchByRoomIdJoiningUser(roomId: UUID): MutableList<UserWithMember> {
        return ctx()
            .select()
            .from(MEMBERS.join(USERS).on(MEMBERS.USER_ID.eq(USERS.ID)))
            .where(MEMBERS.ROOM_ID.eq(roomId))
            .fetch {
                val user = it.into(USERS)
                val member = it.into(MEMBERS)
                UserWithMember(
                    user.id,
                    user.isEnabled,
                    user.name,
                    user.email,
                    member.into(Member::class.java)
                )
            }
    }

    fun fetchByUserIdJoiningMember(userId: UUID): MutableList<UserWithMember> {
        return ctx()
            .select()
            .from(MEMBERS.join(USERS).on(MEMBERS.USER_ID.eq(USERS.ID)))
            .where(USERS.ID.eq(userId))
            .fetch {
                val user = it.into(USERS)
                val member = it.into(MEMBERS)
                UserWithMember(
                    user.id,
                    user.isEnabled,
                    user.name,
                    user.email,
                    member.into(Member::class.java)
                )
            }
    }

    fun fetchByUserIdAndRoomId(userId: UUID, roomId: UUID): Member? {
        return ctx()
            .select()
            .from(MEMBERS)
            .where(MEMBERS.USER_ID.eq(userId).and(MEMBERS.ROOM_ID.eq(roomId)))
            .fetchOneInto(Member::class.java)
    }
}