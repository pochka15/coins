package pw.coins.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.MembersDao
import pw.coins.db.generated.tables.daos.UsersDao
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.User
import pw.coins.user.dtos.UserCredentials

@Service
class UserSe(
    private val passwordEncoder: PasswordEncoder,
    private val membersDao: MembersDao,
    private val usersDao: UsersDao,
) {
    fun createUser(credentials: UserCredentials): User {
        val user = credentials.toUser(passwordEncoder)

        usersDao.insert(user)

        assert(user.id != null) {
            "Couldn't create user with the name ${credentials.name}, returned Id is null"
        }

        return user
    }

    fun findByName(username: String): User? {
        return usersDao.fetchByName(username).firstOrNull()
    }

    fun removeUserById(id: Long) {
        usersDao.deleteById(id)
    }

    fun findAssociatedMembers(userId: Long): List<Member> {
        return membersDao.fetchByUserId(userId)
    }
}

private fun UserCredentials.toUser(passwordEncoder: PasswordEncoder): User {
    val x = User()
    x.name = name
    x.password = passwordEncoder.encode(password)
    x.email = email
    x.isEnabled = true
    return x
}
