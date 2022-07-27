package pw.coins.room.model

import org.jooq.Configuration
import org.springframework.stereotype.Component
import pw.coins.db.generated.Tables.*
import pw.coins.db.generated.tables.pojos.Room
import java.util.*
import pw.coins.db.generated.tables.daos.RoomsDao as OriginalDao

@Component
class RoomsDao(
    val configuration: Configuration
) : OriginalDao(configuration) {

    fun fetchUserRooms(userId: UUID): List<Room> {
        return ctx()
            .select()
            .from(USERS)
            .join(MEMBERS).on(USERS.ID.eq(MEMBERS.USER_ID))
            .join(ROOMS).on(ROOMS.ID.eq(MEMBERS.ROOM_ID))
            .where(USERS.ID.eq(userId))
            .fetchInto(Room::class.java)
    }
}