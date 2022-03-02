package pw.coins.bot.dtos

/**
 * Dto representing MS Teams Conversation structure
 */
data class Conversation(

    val conversationType: String,
    val tenantId: String,
    val id: String
)