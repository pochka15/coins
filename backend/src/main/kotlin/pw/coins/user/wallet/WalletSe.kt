package pw.coins.user.wallet

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.WalletsDao
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.user.wallet.dtos.NewWallet

@Service
class WalletSe(val walletsDao: WalletsDao) {
    fun getWalletById(walletId: Long): Wallet? {
        return walletsDao.findById(walletId)
    }

    fun createWallet(newWallet: NewWallet): Wallet {
        val wallet = newWallet.toWallet()
        walletsDao.insert(wallet)

        assert(wallet.id != null) {
            "Couldn't create wallet with the name ${newWallet.name} = ${wallet.id}, returned Id is null"
        }

        return wallet
    }

    fun getUserWallets(userId: Long): List<Wallet> {
        return walletsDao.fetchByOwnerId(userId)
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
