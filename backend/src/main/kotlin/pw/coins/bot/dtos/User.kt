package pw.coins.bot.dtos

/**
 * Dto representing MS Teams User structure
 */
data class User(

    val id: String,
    val name: String,
    val aadObjectId: String
)