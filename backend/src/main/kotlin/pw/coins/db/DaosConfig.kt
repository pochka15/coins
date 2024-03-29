package pw.coins.db

import org.jooq.Configuration
import org.springframework.context.annotation.Bean
import pw.coins.db.generated.tables.daos.CoinsLocksDao
import pw.coins.db.generated.tables.daos.UsersDao
import pw.coins.db.generated.tables.daos.UsosTokensDao
import pw.coins.db.generated.tables.daos.UsosUsersDao

@org.springframework.context.annotation.Configuration
class DaosConfig(
    val configuration: Configuration
) {
    @Bean
    fun usersDao(): UsersDao = UsersDao(configuration)

    @Bean
    fun usosTokensDao(): UsosTokensDao = UsosTokensDao(configuration)

    @Bean
    fun coinsLocksDao(): CoinsLocksDao = CoinsLocksDao(configuration)

    @Bean
    fun usosUsersDao(): UsosUsersDao = UsosUsersDao(configuration)
}