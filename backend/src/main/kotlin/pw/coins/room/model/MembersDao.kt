package pw.coins.room.model

import org.jooq.Configuration
import org.springframework.stereotype.Component
import pw.coins.db.generated.Tables.*
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

    fun fetchByRoomIdAndUserIdJoiningWallet(userId: UUID, roomId: UUID): MemberWithWallet? {
        return ctx()
            .select()
            .from(MEMBERS.join(WALLETS).on(WALLETS.MEMBER_ID.eq(MEMBERS.ID)))
            .where(MEMBERS.USER_ID.eq(userId).and(MEMBERS.ROOM_ID.eq(roomId)))
            .fetchOne {
                val member = it.into(MEMBERS)
                val wallet = it.into(WALLETS)
                MemberWithWallet(
                    member.id,
                    member.userId,
                    member.roomId,
                    wallet.id,
                    wallet.coinsAmount
                )
            }

    }
}