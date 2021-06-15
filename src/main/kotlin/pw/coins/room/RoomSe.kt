package pw.coins.room

import au.com.console.jpaspecificationdsl.and
import au.com.console.jpaspecificationdsl.get
import au.com.console.jpaspecificationdsl.where
import org.springframework.stereotype.Service
import pw.coins.db.specifications.fetch
import pw.coins.room.dtos.NewRoom
import pw.coins.room.dtos.Room
import pw.coins.room.member.MemberEn
import pw.coins.room.member.MemberRe
import pw.coins.room.member.Role
import pw.coins.room.member.dtos.Member
import pw.coins.room.member.dtos.NewMember
import pw.coins.user.UserRe

@Service
class RoomSe(
    private val roomRe: RoomRe,
    private val memberRe: MemberRe,
    private val userRe: UserRe
) {
    fun create(newRoom: NewRoom): Room {
        return roomRe.save(RoomEn(name = newRoom.name)).toRoom()
    }

    fun addMember(roomId: Long, member: NewMember): Member {
        val memberEn =
            MemberEn(
                room = roomRe.getOne(roomId),
                roles = mutableSetOf(Role.USER),
                associatedUser = userRe.getOne(member.associatedUserId)
            )
        return memberRe.save(memberEn).toMember()
    }

    fun getMembers(roomId: Long): List<Member> {
        return memberRe.findAll(
            where<MemberEn> { root -> equal(root.get(MemberEn::room).get(RoomEn::id), roomId) }
                    and fetch(MemberEn::roles)
                    and fetch(MemberEn::associatedUser)
        ).map { it.toMember() }
    }

    fun removeMemberById(memberId: Long) {
        memberRe.deleteById(memberId)
    }
}