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
    fun removeUserById(id: UUID) = usersDao.deleteById(id)
    fun getUserById(id: UUID): User? = usersDao.fetchOneById(id)
    fun getUserByEmail(email: String): User? = usersDao.fetchByEmail(email).getOrNull(0)
    fun findAssociatedMembers(userId: UUID): List<UserWithMember> = membersDao.fetchByUserIdJoiningMember(userId)

    fun createUser(userName: String, email: String = ""): User {
        return User(uuidSource.genUuid(), true, userName, email)
            .also { usersDao.insert(it) }
    }

}
