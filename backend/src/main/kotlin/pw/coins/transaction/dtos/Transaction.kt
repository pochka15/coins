package pw.coins.transaction.dtos

/**
 * Dto representing a single coins transaction
 */
data class Transaction(val fromWalletId: Long, val toWalletId: Long, val amount: Int)