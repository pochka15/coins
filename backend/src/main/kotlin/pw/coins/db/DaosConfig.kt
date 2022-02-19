package pw.coins.db

import org.jooq.Configuration
import org.springframework.context.annotation.Bean
import pw.coins.db.generated.tables.daos.*

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

    @Bean
    fun tasksDao(): TasksDao = TasksDao(configuration)

}