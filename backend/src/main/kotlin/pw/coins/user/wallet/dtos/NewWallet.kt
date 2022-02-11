package pw.coins.user.wallet.dtos

data class NewWallet(val name: String, val coinsAmount: Int, val ownerId: Long)