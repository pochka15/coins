package pw.coins.room.member

import org.hibernate.Hibernate
import pw.coins.room.RoomEn
import pw.coins.room.member.dtos.Member
import pw.coins.user.UserEn
import javax.persistence.*

@Entity
@Table(name = "members")
class MemberEn(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    var associatedUser: UserEn? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id", nullable = false)
    var room: RoomEn? = null,

    @ElementCollection(targetClass = Role::class, fetch = FetchType.LAZY)
    @CollectionTable(name = "member_role", joinColumns = [JoinColumn(name = "member_id")])
    @Enumerated(EnumType.STRING)
    var roles: MutableSet<Role> = mutableSetOf()
) {
    fun toMember(): Member = Member(id!!, roles)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as MemberEn

        return id != null && id == other.id
    }

    override fun hashCode(): Int = 1183252619

}