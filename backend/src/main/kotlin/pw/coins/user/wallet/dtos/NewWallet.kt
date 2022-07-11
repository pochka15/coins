package pw.coins.user.wallet.dtos

import java.util.*

data class NewWallet(val name: String, val coinsAmount: Int, val ownerId: UUID)