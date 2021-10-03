package pw.coins.user.wallet.dtos

import pw.coins.user.wallet.WalletEn

data class NewWallet(val name: String, val coinsAmount: Int) {
    fun toWalletEn(): WalletEn = WalletEn(name = name, coinsAmount = coinsAmount)
}