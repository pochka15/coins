package pw.coins.room.model

import java.util.*

data class MemberWithWallet(
    val memberId: UUID,
    val userId: UUID,
    val roomId: UUID,
    val walletId: UUID,
    val coinsAmount: Int,
)
