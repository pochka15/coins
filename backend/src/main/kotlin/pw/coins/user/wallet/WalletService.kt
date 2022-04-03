package pw.coins.user.wallet

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.WalletsDao
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.user.wallet.dtos.NewWallet
import pw.coins.user.wallet.dtos.Transaction

@Service
class WalletService(val walletsDao: WalletsDao) {
    fun getWalletById(walletId: Long): Wallet? {
        return walletsDao.findById(walletId)
    }

    fun createWallet(newWallet: NewWallet): Wallet {
        val wallet = newWallet.toWallet()
        walletsDao.insert(wallet)

        if (wallet.id == null) {
            throw Exception("Couldn't create wallet with the name ${newWallet.name} = ${wallet.id}, returned Id is null")
        }

        return wallet
    }

    fun getUserWallets(userId: Long): List<Wallet> {
        return walletsDao.fetchByOwnerId(userId)
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

private fun NewWallet.toWallet(): Wallet {
    return Wallet(
        null,
        coinsAmount,
        name,
        ownerId
    )
}
