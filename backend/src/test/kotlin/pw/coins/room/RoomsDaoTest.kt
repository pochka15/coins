package pw.coins.room

import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pw.coins.db.DaosConfig
import pw.coins.db.generated.public_.tables.daos.RoomsDao
import pw.coins.db.generated.public_.tables.pojos.Rooms

@JooqTest
@ContextConfiguration(classes = [DaosConfig::class])
@ActiveProfiles("db-postgresql")
class RoomsDaoTest(
    @Autowired val roomsDao: RoomsDao
) {

    @Test
    fun tmp() {
        val roomsT = Rooms()
        roomsT.name = "kek room"
        roomsDao.insert(roomsT)

        roomsT.id.shouldNotBeNull()
    }
}