package pw.coins.bot

import org.springframework.stereotype.Service
import pw.coins.bot.dtos.User
import pw.coins.db.generated.tables.daos.TeamsUsersDao
import pw.coins.db.generated.tables.pojos.TeamsUser

@Service
class TeamsUserService(val teamsUsersDao: TeamsUsersDao) {

    /**
     * Store into the db an MS Teams user which is bound to the given usual user
     */
    fun createTeamsUser(
        user: User,
        email: String?,
        originalUserId: Long
    ): TeamsUser {
        val x = TeamsUser(user.id, user.name, user.aadObjectId, email, originalUserId)
        teamsUsersDao.insert(x)
        return x
    }

    /**
     * Get an ID of the User model which is bound to the TeamsUser.
     */
    fun getOriginalUserId(teamsUserId: String): Long? {
        val user = teamsUsersDao.fetchById(teamsUserId).firstOrNull() ?: return null
        return user.originalUserId
    }
}