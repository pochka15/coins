package pw.coins.room.member

import au.com.console.jpaspecificationdsl.and
import au.com.console.jpaspecificationdsl.get
import au.com.console.jpaspecificationdsl.where
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import pw.coins.db.specifications.fetch
import pw.coins.room.RoomEn

interface MemberRe : JpaRepository<MemberEn, Long>, JpaSpecificationExecutor<MemberEn> {
    fun fetchRoomMembers(roomEn: RoomEn): List<MemberEn> =
        findAll(
            where<MemberEn> { root -> equal(root.get(MemberEn::room).get(RoomEn::id), roomEn.id!!) }
                    and fetch(MemberEn::roles)
                    and fetch(MemberEn::associatedUser)
        )
}