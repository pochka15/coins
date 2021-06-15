package pw.coins.room.member

import pw.coins.room.RoomEn
import pw.coins.user.UserEn
import java.util.*

fun tmpMemberEn(user: UserEn, room: RoomEn) =
    MemberEn(associatedUser = user, room = room, roles = Collections.singleton(Role.USER))