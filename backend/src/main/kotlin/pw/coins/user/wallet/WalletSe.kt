package pw.coins.user.wallet

import org.springframework.stereotype.Service
import pw.coins.db.generated.public_.tables.daos.WalletsDao
import pw.coins.db.generated.public_.tables.pojos.Wallet
import pw.coins.user.wallet.dtos.NewWallet

@Service
class WalletSe(val walletsDao: WalletsDao) {
    fun getWalletById(walletId: Long): Wallet? {
        return walletsDao.findById(walletId)
    }

    fun createWallet(newWallet: NewWallet): Wallet {
        val wallet = Wallet().apply {
            name = newWallet.name
            coinsAmount = newWallet.coinsAmount
            ownerId = newWallet.ownerId
        }
        walletsDao.insert(wallet)

        assert(wallet.id != null) {
            "Couldn't create wallet with the name ${newWallet.name} = ${wallet.id}, returned Id is null"
        }

        return wallet
    }
}
