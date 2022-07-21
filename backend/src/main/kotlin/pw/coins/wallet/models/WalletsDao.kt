package pw.coins.wallet.models

import org.jooq.Configuration
import org.springframework.stereotype.Component
import pw.coins.db.generated.Tables.MEMBERS
import pw.coins.db.generated.Tables.WALLETS
import java.util.*
import pw.coins.db.generated.tables.daos.WalletsDao as OriginalDao

@Component
class WalletsDao(
    val configuration: Configuration
) : OriginalDao(configuration) {

    fun fetchByUserIdAndRoomId(userId: UUID, roomId: UUID): ExtendedWallet? {
        return ctx()
            .select()
            .from(WALLETS.join(MEMBERS).on(WALLETS.MEMBER_ID.eq(MEMBERS.ID)))
            .where(MEMBERS.USER_ID.eq(userId).and(MEMBERS.ROOM_ID.eq(roomId)))
            .fetchOne {
                val member = it.into(MEMBERS)
                val wallet = it.into(WALLETS)
                ExtendedWallet(
                    member.id,
                    member.userId,
                    member.roomId,
                    wallet.id,
                    wallet.coinsAmount
                )
            }
    }

    fun fetchExtendedWalletById(walletId: UUID): ExtendedWallet? {
        return ctx()
            .select()
            .from(MEMBERS.join(WALLETS).on(WALLETS.MEMBER_ID.eq(MEMBERS.ID)))
            .where(WALLETS.ID.eq(walletId))
            .fetchOne {
                val member = it.into(MEMBERS)
                val wallet = it.into(WALLETS)
                ExtendedWallet(
                    member.id,
                    member.userId,
                    member.roomId,
                    wallet.id,
                    wallet.coinsAmount
                )
            }
    }
}