package pw.coins.user.wallet

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import pw.coins.db.generated.tables.pojos.Wallet

@RestController
@RequestMapping("/user/wallet")
@Tag(name = "Wallet")
class WalletController(val walletService: WalletService) {
    @PostMapping
    fun createWallet(wallet: NewWallet): WalletData {
        return walletService.createWallet(wallet).toData()
    }

    @GetMapping("/{walletId}")
    fun wallet(@PathVariable walletId: String): WalletData? {
        return walletService.getWalletById(walletId)?.toData()
    }
}

data class WalletData(
    val id: String,
    val coinsAmount: Int,
    val name: String,
    val ownerId: String,
)

fun Wallet.toData(): WalletData {
    return WalletData(
        id = id.toString(),
        coinsAmount = coinsAmount,
        name = name,
        ownerId = ownerId.toString(),
    )
}
