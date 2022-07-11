package pw.coins.user.wallet.dtos

import java.util.*

/**
 * Dto representing a single coins transaction
 */
data class Transaction(val fromWalletId: UUID, val toWalletId: UUID, val amount: Int)