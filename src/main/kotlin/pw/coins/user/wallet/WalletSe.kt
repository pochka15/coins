package pw.coins.user.wallet

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pw.coins.user.wallet.dtos.NewWallet
import pw.coins.user.wallet.dtos.Wallet

@Service
class WalletSe(val walletRe: WalletRe) {
    fun getWalletById(walletId: Long): Wallet =
        walletRe.findByIdOrNull(walletId)!!.toWallet()

    private fun WalletEn.toWallet(): Wallet = Wallet(name, coinsAmount)

    fun createWallet(wallet: NewWallet): Wallet =
        walletRe.save(wallet.toWalletEn()).toWallet()
}
