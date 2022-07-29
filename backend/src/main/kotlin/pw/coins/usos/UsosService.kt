package pw.coins.usos

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.UsosUser.USOS_USERS
import pw.coins.db.generated.tables.daos.UsosUsersDao
import pw.coins.db.generated.tables.pojos.UsosUser


@Service
class UsosService(val usosUsersDao: UsosUsersDao) {
    fun getUsosUserById(id: String): UsosUser? = usosUsersDao.fetchOneById(id)
    fun createUsosUser(usosUser: UsosUser): UsosUser = createUsosUsers(listOf(usosUser)).single()

    fun createUsosUsers(usosUsers: Collection<UsosUser>): Collection<UsosUser> {
        return usosUsers.also { usosUsersDao.insert(it) }
    }

    fun getUsosUsersByIds(ids: Collection<String>): List<UsosUser> {
        return usosUsersDao.fetch(USOS_USERS.ID, ids)
    }
}