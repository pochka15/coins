package pw.coins.user

import au.com.console.jpaspecificationdsl.get
import au.com.console.jpaspecificationdsl.where
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pw.coins.room.member.MemberEn
import pw.coins.room.member.MemberRe
import pw.coins.room.member.dtos.Member
import pw.coins.user.dtos.User
import pw.coins.user.dtos.UserCredentials

@Service
class UserSe(
    private val userRe: UserRe,
    private val passwordEncoder: PasswordEncoder,
    private val memberRe: MemberRe
) {
    fun createUser(credentials: UserCredentials): User {
        return userRe.save(credentials.toEntity(passwordEncoder)).toUser()
    }

    fun findByName(username: String): User? {
        return userRe.findByName(username)?.toUser()
    }

    fun findById(id: Long): User? {
        return userRe.findById(id).orElse(null)?.toUser()
    }

    fun removeUserById(id: Long) {
        userRe.deleteById(id)
    }

    fun findAssociatedMembers(userId: Long): List<Member> {
        fun spec(): Specification<MemberEn> =
            where {
                equal(
                    it.get(MemberEn::associatedUser).get(UserEn::id),
                    userId
                )
            }
        return memberRe.findAll(spec()).map { it.toMember() }
    }
}