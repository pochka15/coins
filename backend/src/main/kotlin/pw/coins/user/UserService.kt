package pw.coins.user

import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.UsersDao
import pw.coins.db.generated.tables.pojos.User
import pw.coins.room.model.MembersDao
import pw.coins.room.model.UserWithMember
import pw.coins.security.UuidSource
import java.util.*

@Service
class UserService(
    private val membersDao: MembersDao,
    private val usersDao: UsersDao,
    private val uuidSource: UuidSource,
) {
    fun createUser(userName: String, email: String = ""): User {
        val user = User(uuidSource.genUuid(), true, userName, email)

        usersDao.insert(user)

        if (user.id == null) {
            throw Exception("Couldn't create user with the name '$userName', returned Id is null")
        }

        return user
    }

    fun removeUserById(id: UUID) = usersDao.deleteById(id)

    fun getUserById(id: UUID): User? = usersDao.fetchOneById(id)

    fun findAssociatedMembers(userId: UUID): List<UserWithMember> {
        return membersDao.fetchByUserIdJoiningMember(userId)
    }

    fun getUser(email: String): User? {
        return usersDao.fetchByEmail(email).getOrNull(0)
    }
}
