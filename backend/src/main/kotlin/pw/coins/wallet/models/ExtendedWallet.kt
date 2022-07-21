package pw.coins.wallet.models

import java.util.*

data class ExtendedWallet(
    val memberId: UUID,
    val userId: UUID,
    val roomId: UUID,
    val walletId: UUID,
    val coinsAmount: Int,
)
