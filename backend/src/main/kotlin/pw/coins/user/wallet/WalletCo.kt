package pw.coins.user.wallet

import org.springframework.web.bind.annotation.*
import pw.coins.user.wallet.dtos.NewWallet
import pw.coins.user.wallet.dtos.Wallet

@RestController
@RequestMapping("/user/wallet")
class WalletCo(val walletSe: WalletSe) {
    @PostMapping
    fun createWallet(wallet: NewWallet): Wallet {
        return walletSe.createWallet(wallet)
    }

    @GetMapping("/{walletId}")
    fun wallet(@PathVariable walletId: Long): Wallet {
        return walletSe.getWalletById(walletId)
    }
}