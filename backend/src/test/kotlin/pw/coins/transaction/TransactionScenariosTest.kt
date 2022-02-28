package pw.coins.transaction

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pw.coins.transaction.dtos.Transaction
import pw.coins.user.UserSe
import pw.coins.user.wallet.WalletSe
import pw.coins.user.wallet.dtos.NewWallet

@SpringBootTest
class TransactionScenariosTest(
    @Autowired val transactionSe: TransactionSe,
    @Autowired val userSe: UserSe,
    @Autowired val walletSe: WalletSe
) {

    @Test
    fun `make transaction with not enough money EXPECT exception is thrown`() {
        val fromAmount = 1
        val toAmount = 0
        val transactionAmount = 2
        val fromUser = userSe.createUser("from")
        val toUser = userSe.createUser("to")
        val fromWallet = walletSe.createWallet(NewWallet("from", fromAmount, fromUser.id))
        val toWallet = walletSe.createWallet(NewWallet("to", toAmount, toUser.id))

        assertThrows<AssertionError> {
            transactionSe.executeTransaction(
                Transaction(
                    fromWallet.id, toWallet.id, transactionAmount
                )
            )
        }
    }

    @Test
    fun `make transaction with not enough money EXPECT coins are not sent`() {
        val fromAmount = 1
        val toAmount = 0
        val transactionAmount = 2

        val fromUser = userSe.createUser("from")
        val toUser = userSe.createUser("to")

        val fromWallet = walletSe.createWallet(NewWallet("from", fromAmount, fromUser.id))
        val toWallet = walletSe.createWallet(NewWallet("to", toAmount, toUser.id))

        val fromWalletAmountBefore = fromWallet.coinsAmount
        val toWalletAmountBefore = toWallet.coinsAmount

//        Omit AssertionError
        assertThrows<AssertionError> {
            transactionSe.executeTransaction(
                Transaction(
                    fromWallet.id, toWallet.id, transactionAmount
                )
            )
        }

        val fromWalletAfter = walletSe.getWalletById(fromWallet.id)
        val toWalletAfter = walletSe.getWalletById(toWallet.id)

        assertThat(fromWalletAmountBefore).isEqualTo(fromWalletAfter!!.coinsAmount)
        assertThat(toWalletAmountBefore).isEqualTo(toWalletAfter!!.coinsAmount)
    }

}