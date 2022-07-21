package pw.coins.room.model

import org.jooq.Configuration
import org.springframework.stereotype.Component
import pw.coins.db.generated.Tables.MEMBERS
import pw.coins.db.generated.Tables.ROOMS
import pw.coins.db.generated.tables.pojos.Room
import java.util.*
import pw.coins.db.generated.tables.daos.RoomsDao as OriginalDao

@Component
class RoomsDao(
    val configuration: Configuration
) : OriginalDao(configuration) {

    fun fetchRoomByMemberId(memberId: UUID): Room? {
        return ctx()
            .select()
            .from(MEMBERS.join(ROOMS).on(MEMBERS.ROOM_ID.eq(ROOMS.ID)))
            .where(MEMBERS.ID.eq(memberId))
            .fetchOneInto(Room::class.java)
    }
}