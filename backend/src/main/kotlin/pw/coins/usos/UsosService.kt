package pw.coins.usos

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.UsosUsersDao
import pw.coins.db.generated.tables.pojos.UsosUser
import java.util.*


@Service
class UsosService(val usosUsersDao: UsosUsersDao) {
    fun getUsosUserById(id: String): UsosUser? = usosUsersDao.fetchOneById(id)

    fun createUsosUser(apiUsosUser: ApiUsosUser, userId: UUID?): UsosUser {
        return with(apiUsosUser) {
            UsosUser(id, firstName, lastName, email, userId)
                .also { usosUsersDao.insert(it) }
        }
    }
}