package pw.coins.bot.dtos

/**
 * Dto representing MS Teams ConversationReference
 */
data class ConversationReference(

    val activityId: String,
    val user: User,
    val bot: Bot,
    val conversation: Conversation,
    val channelId: String,
    val locale: String,
    val serviceUrl: String
)