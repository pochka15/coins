package pw.coins.wallet

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import pw.coins.db.generated.tables.pojos.User
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.security.PrincipalContext
import pw.coins.wallet.models.ExtendedWallet

@RestController
@RequestMapping("/wallet")
@Tag(name = "Wallet")
class WalletController(val walletService: WalletService) {
    @PostMapping
    fun createWallet(wallet: NewWallet): WalletData {
        return walletService.createWallet(wallet).toData()
    }

    @GetMapping("/{walletId}")
    fun wallet(@PathVariable walletId: String, @PrincipalContext user: User): WalletData {
        val wallet = walletService.getWalletById(walletId)
            ?: throw ResponseStatusException(BAD_REQUEST, "Couldn't find a wallet by id = $walletId")
        if (wallet.userId != user.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permissions to get the wallet")
        }
        return wallet.toData()
    }

    @GetMapping
    fun walletByRoomId(@RequestParam roomId: String, @PrincipalContext user: User): WalletData {
        return walletService.getWalletByRoomIdAndUserId(roomId, user.id.toString())?.toData()
            ?: throw ResponseStatusException(
                BAD_REQUEST,
                "Couldn't find a wallet for the user '${user.name}' in the roomId = $roomId"
            )
    }
}

private fun ExtendedWallet.toData(): WalletData {
    return WalletData(walletId.toString(), coinsAmount, memberId.toString())
}

data class WalletData(
    val id: String,
    val coinsAmount: Int,
    val memberId: String,
)

fun Wallet.toData(): WalletData {
    return WalletData(
        id = id.toString(),
        coinsAmount = coinsAmount,
        memberId = memberId.toString(),
    )
}
