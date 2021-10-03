package pw.coins.room

import org.hibernate.Hibernate
import pw.coins.room.dtos.Room
import pw.coins.room.member.MemberEn
import javax.persistence.*

@Entity
@Table(name = "rooms")
class RoomEn(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    var name: String? = null,

    @OneToMany(
        mappedBy = "room",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var members: MutableList<MemberEn> = mutableListOf()
) {
    fun toRoom() = Room(name!!, id!!)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as RoomEn

        return id != null && id == other.id
    }

    override fun hashCode(): Int = 1279775882
}