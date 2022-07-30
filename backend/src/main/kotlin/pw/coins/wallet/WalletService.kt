package pw.coins.wallet

import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import pw.coins.db.generated.Tables.COINS_LOCKS
import pw.coins.db.generated.Tables.WALLETS
import pw.coins.db.generated.tables.daos.CoinsLocksDao
import pw.coins.db.generated.tables.pojos.CoinsLock
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.security.UuidSource
import pw.coins.wallet.models.ExtendedWallet
import pw.coins.wallet.models.Transaction
import pw.coins.wallet.models.WalletsDao
import java.util.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Service
class WalletService(
    val walletsDao: WalletsDao,
    val uuidSource: UuidSource,
    val dslContext: DSLContext,
    val locksDao: CoinsLocksDao,
) {
    fun getWalletById(id: UUID): ExtendedWallet? = walletsDao.fetchExtendedWalletById(id)
    fun getWalletByMemberId(id: UUID): Wallet? = walletsDao.fetchByMemberId(id).getOrNull(0)
    fun createWallet(newWallet: NewWallet): Wallet = createWallets(listOf(newWallet)).single()

    fun createWallets(wallets: Collection<NewWallet>): List<Wallet> {
        return wallets.map {
            Wallet(uuidSource.genUuid(), it.coinsAmount, it.memberId)
        }.also { walletsDao.insert(it) }
    }

    fun getWalletByRoomIdAndUserId(roomId: UUID, userId: UUID): ExtendedWallet? {
        return walletsDao.fetchByUserIdAndRoomId(userId, roomId)
    }

    /**
     * This method is primarily made to lock coins when creating a new task
     */
    fun lockCoins(wallet: Wallet, taskId: UUID, coinsAmount: Int) {
        val newAmount = wallet.coinsAmount - coinsAmount
        if (newAmount < 0) throw NotEnoughCoinsException("You don't have enough coins to create a task")

        dslContext.transaction { c: Configuration ->
            with(c.dsl()) {
                executeInsert(
                    newRecord(
                        COINS_LOCKS,
                        CoinsLock(uuidSource.genUuid(), coinsAmount, wallet.id, taskId)
                    )
                )
                executeUpdate(
                    newRecord(
                        WALLETS, wallet.also { it.coinsAmount = newAmount }
                    )
                )
            }
        }
    }

    fun transferLockedCoins(lock: CoinsLock, wallet: Wallet) {
        wallet.coinsAmount += lock.amount
        walletsDao.update(wallet)
    }

    fun deleteLockById(id: UUID) {
        locksDao.deleteById(id)
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
    @field:Min(1) @field:Max(1000_000, message = "too big amount of coins")
    val coinsAmount: Int,
    val memberId: UUID,
)

class WalletNotFoundException(message: String?) : RuntimeException(message)
class NotEnoughCoinsException(message: String?) : RuntimeException(message)
class LockNotFoundException(message: String?) : RuntimeException(message)


