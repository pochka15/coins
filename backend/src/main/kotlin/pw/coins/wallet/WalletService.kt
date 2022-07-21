package pw.coins.wallet

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.db.parseUUID
import pw.coins.security.UuidSource
import pw.coins.wallet.models.ExtendedWallet
import pw.coins.wallet.models.Transaction
import pw.coins.wallet.models.WalletsDao
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Service
class WalletService(
    val walletsDao: WalletsDao,
    val uuidSource: UuidSource,
) {
    fun getWalletById(id: String): ExtendedWallet? {
        return walletsDao.fetchExtendedWalletById(parseUUID(id))
    }

    fun createWallet(newWallet: NewWallet): Wallet {
        val wallet = Wallet(
            uuidSource.genUuid(),
            newWallet.coinsAmount,
            parseUUID(newWallet.memberId)
        )
        walletsDao.insert(wallet)
        return wallet
    }

    fun getWalletByRoomIdAndUserId(roomId: String, userId: String): ExtendedWallet? {
        return walletsDao.fetchByUserIdAndRoomId(parseUUID(userId), parseUUID(roomId))
    }

    /**
     * Send coins from one wallet to another making some assertions
     */
    fun executeTransaction(transaction: Transaction) {
        val wallets = walletsDao.fetchById(
            transaction.fromWalletId,
            transaction.toWalletId
        )

        if (transaction.amount == 0) return

        if (transaction.amount < 0) {
            throw Exception("Transaction amount cannot be negative")
        }

        val from = wallets.find { it.id == transaction.fromWalletId }
            ?: throw Exception("Couldn't find the 'from' wallet with an id: ${transaction.fromWalletId}")

        assert(from.coinsAmount >= transaction.amount) { "Cannot execute transaction sender doesn't have enough coins in wallet. Expected to have at list ${transaction.amount} coins" }

        val to = wallets.find { it.id == transaction.toWalletId }
            ?: throw Exception("Couldn't find the 'to' wallet with an id: ${transaction.toWalletId}")

        from.coinsAmount -= transaction.amount
        to.coinsAmount += transaction.amount

        walletsDao.update(from, to)
    }
}

data class NewWallet(
    @field:Min(1) @field:Max(1000_000, message = "Too big amount of coins")
    val coinsAmount: Int,
    @field:NotBlank
    val memberId: String,
)

