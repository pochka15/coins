package pw.coins.wallet.models

import java.util.*

/**
 * Model representing a single coins transaction
 */
data class Transaction(val fromWalletId: UUID, val toWalletId: UUID, val amount: Int)