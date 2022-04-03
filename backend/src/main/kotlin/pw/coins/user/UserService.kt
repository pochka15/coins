package pw.coins.user

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.MembersDao
import pw.coins.db.generated.tables.daos.UsersDao
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.User

@Service
class UserService(
    private val membersDao: MembersDao,
    private val usersDao: UsersDao,
) {
    fun createUser(userName: String): User {
        val user = User(null, true, userName)

        usersDao.insert(user)

        if (user.id == null) {
            throw Exception("Couldn't create user with the name '$userName', returned Id is null")
        }

        return user
    }

    fun removeUserById(id: Long) = usersDao.deleteById(id)

    fun getUserById(id: Long): User? = usersDao.fetchOneById(id)

    fun findAssociatedMembers(userId: Long): List<Member> {
        return membersDao.fetchByUserId(userId)
    }
}
