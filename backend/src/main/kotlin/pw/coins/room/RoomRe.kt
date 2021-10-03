package pw.coins.room

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface RoomRe : JpaRepository<RoomEn, Long>, JpaSpecificationExecutor<RoomEn> {

    fun generateTmpRooms(vararg roomNames: String): List<RoomEn> {
        val rooms = roomNames.map { save(RoomEn(name = it)) }
        flush()
        return rooms
    }
}