package pw.coins.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pw.coins.db.generated.public_.tables.daos.MembersDao
import pw.coins.db.generated.public_.tables.daos.UsersDao
import pw.coins.db.generated.public_.tables.pojos.Member
import pw.coins.db.generated.public_.tables.pojos.User
import pw.coins.user.dtos.UserCredentials

@Service
class UserSe(
    private val passwordEncoder: PasswordEncoder,
    private val membersDao: MembersDao,
    private val usersDao: UsersDao,
) {
    fun createUser(credentials: UserCredentials): User {
        val user = User().apply {
            name = credentials.name
            password = passwordEncoder.encode(credentials.password)
            email = credentials.email
            isEnabled = true
        }
        usersDao.insert(user)

        assert(user.id != null) {
            "Couldn't create user with the name ${credentials.name}, returned Id is null"
        }

        return user
    }

    fun findByName(username: String): User? {
        return usersDao.fetchOneByName(username)
    }

    fun removeUserById(id: Long) {
        usersDao.deleteById(id)
    }

    fun findAssociatedMembers(userId: Long): List<Member> {
        return membersDao.fetchByUserId(userId)
    }
}