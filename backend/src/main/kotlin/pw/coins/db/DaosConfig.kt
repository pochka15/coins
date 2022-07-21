package pw.coins.db

import org.jooq.Configuration
import org.springframework.context.annotation.Bean
import pw.coins.db.generated.tables.daos.UsersDao
import pw.coins.db.generated.tables.daos.UsosTokensDao

@org.springframework.context.annotation.Configuration
class DaosConfig(
    val configuration: Configuration
) {

    @Bean
    fun usersDao(): UsersDao = UsersDao(configuration)

    @Bean
    fun usosTokensDao(): UsosTokensDao = UsosTokensDao(configuration)
}