package pw.coins.room.member.dtos

import pw.coins.room.member.Role

data class Member(
    var id: Long,
    var roles: MutableSet<Role> = mutableSetOf(),
)
