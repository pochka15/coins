package pw.coins.db

import org.jooq.Configuration
import org.springframework.context.annotation.Bean
import pw.coins.db.generated.public_.tables.daos.MembersDao
import pw.coins.db.generated.public_.tables.daos.RoomsDao
import pw.coins.db.generated.public_.tables.daos.UsersDao
import pw.coins.db.generated.public_.tables.daos.WalletsDao

@org.springframework.context.annotation.Configuration
class DaosConfig(val configuration: Configuration) {

    @Bean
    fun roomsDao(): RoomsDao = RoomsDao(configuration)

    @Bean
    fun membersDao(): MembersDao = MembersDao(configuration)

    @Bean
    fun usersDao(): UsersDao = UsersDao(configuration)

    @Bean
    fun walletsDao(): WalletsDao = WalletsDao(configuration)

}