package pw.coins.user.wallet

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.user.wallet.dtos.NewWallet
import pw.coins.user.wallet.dtos.Wallet

@RestController
@RequestMapping("/user/wallet")
@Tag(name = "Wallet")
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