package pw.coins.wallet

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import pw.coins.db.generated.tables.pojos.User
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.room.model.MemberWithWallet
import pw.coins.security.PrincipalContext

@RestController
@RequestMapping("/wallet")
@Tag(name = "Wallet")
class WalletController(val walletService: WalletService) {
    @PostMapping
    fun createWallet(wallet: NewWallet): WalletData {
        return walletService.createWallet(wallet).toData()
    }

    @GetMapping("/{walletId}")
    fun wallet(@PathVariable walletId: String): WalletData {
        return walletService.getWalletById(walletId)?.toData()
            ?: throw ResponseStatusException(BAD_REQUEST, "Couldn't find a wallet by id = $walletId")
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

private fun MemberWithWallet.toData(): WalletData {
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
