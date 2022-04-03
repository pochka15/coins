package pw.coins.user.wallet

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.user.wallet.dtos.NewWallet

@RestController
@RequestMapping("/user/wallet")
@Tag(name = "Wallet")
class WalletController(val walletService: WalletService) {
    @PostMapping
    fun createWallet(wallet: NewWallet): Wallet {
        return walletService.createWallet(wallet)
    }

    @GetMapping("/{walletId}")
    fun wallet(@PathVariable walletId: Long): Wallet? {
        return walletService.getWalletById(walletId)
    }
}