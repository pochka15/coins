package pw.coins.bot.dtos

data class Wallet(
    val name: String,
    val coinsAmount: Int
)

data class Task(
    val title: String,
    val status: String
)

/**
 * Response data that is used in the 'home' endpoint
 */
data class HomeData(
    val wallets: Collection<Wallet>,
    val tasks: Collection<Task>
)
