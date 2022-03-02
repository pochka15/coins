package pw.coins.bot.dtos

/**
 * Income data that is sent by the bot. Used by the 'home' endpoint
 */
data class HomePayload(
    val userId: String
)
