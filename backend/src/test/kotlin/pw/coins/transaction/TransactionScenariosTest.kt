package pw.coins.transaction

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pw.coins.user.UserService
import pw.coins.wallet.WalletService

@SpringBootTest
class TransactionScenariosTest(
    @Autowired val userService: UserService,
    @Autowired val walletService: WalletService
) {

//    @Test
//    fun `make transaction with not enough money EXPECT exception is thrown`() {
//        val fromAmount = 1
//        val toAmount = 0
//        val transactionAmount = 2
//        val fromUser = userService.createUser("from")
//        val toUser = userService.createUser("to")
//        val fromWallet = walletService.createWallet(NewWallet("from", fromAmount, fromUser.id))
//        val toWallet = walletService.createWallet(NewWallet("to", toAmount, toUser.id))
//
//        assertThrows<AssertionError> {
//            walletService.executeTransaction(
//                Transaction(
//                    fromWallet.id, toWallet.id, transactionAmount
//                )
//            )
//        }
//    }
//
//    @Test
//    fun `make transaction with not enough money EXPECT coins are not sent`() {
//        val fromAmount = 1
//        val toAmount = 0
//        val transactionAmount = 2
//
//        val fromUser = userService.createUser("from")
//        val toUser = userService.createUser("to")
//
//        val fromWallet = walletService.createWallet(NewWallet("from", fromAmount, fromUser.id))
//        val toWallet = walletService.createWallet(NewWallet("to", toAmount, toUser.id))
//
//        val fromWalletAmountBefore = fromWallet.coinsAmount
//        val toWalletAmountBefore = toWallet.coinsAmount
//
////        Omit AssertionError
//        assertThrows<AssertionError> {
//            walletService.executeTransaction(
//                Transaction(
//                    fromWallet.id, toWallet.id, transactionAmount
//                )
//            )
//        }
//
//        val fromWalletAfter = walletService.getWalletById(fromWallet.id)
//        val toWalletAfter = walletService.getWalletById(toWallet.id)
//
//        assertThat(fromWalletAmountBefore).isEqualTo(fromWalletAfter!!.coinsAmount)
//        assertThat(toWalletAmountBefore).isEqualTo(toWalletAfter!!.coinsAmount)
//    }

}