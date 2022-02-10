package pw.coins.db

import org.jooq.Configuration
import org.springframework.context.annotation.Bean
import pw.coins.db.generated.public_.tables.daos.RoomsDao

@org.springframework.context.annotation.Configuration
class DaosConfig(val configuration: Configuration) {

    @Bean
    fun roomsDao(): RoomsDao {
        return RoomsDao(configuration)
    }
}