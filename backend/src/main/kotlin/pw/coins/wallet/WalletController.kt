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
import java.util.*

@RestController
@RequestMapping("/wallet")
@Tag(name = "Wallet")
class WalletController(val walletService: WalletService) {
    @PostMapping
    fun createWallet(wallet: NewWallet): WalletData {
        return walletService.createWallet(wallet).toData()
    }

    @GetMapping("/{walletId}")
    fun wallet(@PathVariable walletId: UUID, @PrincipalContext user: User): WalletData {
        val wallet = walletService.getWalletById(walletId)
            ?: throw ResponseStatusException(BAD_REQUEST, "Couldn't find a wallet by id = $walletId")
        if (wallet.userId != user.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permissions to get the wallet")
        }
        return wallet.toData()
    }

    @GetMapping
    fun walletByRoomId(@RequestParam roomId: UUID, @PrincipalContext user: User): WalletData {
        return walletService.getWalletByRoomIdAndUserId(roomId, user.id)?.toData()
            ?: throw ResponseStatusException(
                BAD_REQUEST,
                "Couldn't find a wallet for the user '${user.name}' in the roomId = $roomId"
            )
    }
}

private fun ExtendedWallet.toData(): WalletData {
    return WalletData(walletId, coinsAmount, memberId)
}

data class WalletData(
    val id: UUID,
    val coinsAmount: Int,
    val memberId: UUID,
)

fun Wallet.toData(): WalletData {
    return WalletData(
        id = id,
        coinsAmount = coinsAmount,
        memberId = memberId,
    )
}
