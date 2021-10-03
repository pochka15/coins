package pw.coins.user.dtos

data class User(
    var id: Long,
    var name: String,
    var isEnabled: Boolean = true,
    var email: String,
)