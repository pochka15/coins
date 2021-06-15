package pw.coins.user

import org.hibernate.Hibernate
import pw.coins.room.member.MemberEn
import pw.coins.user.dtos.User
import pw.coins.user.wallet.WalletEn
import javax.persistence.*

@Entity
@Table(name = "users")
class UserEn(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(unique = true)
    var name: String? = null,

    var password: String? = null,
    var isEnabled: Boolean = true,
    var email: String? = null,

    @OneToMany(
        mappedBy = "associatedUser",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
    )
    var associatedMembers: MutableList<MemberEn> = mutableListOf(),

    @OneToMany(
        mappedBy = "owner",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
    )
    var wallet: MutableList<WalletEn> = mutableListOf()
) {
    fun toUser() = User(
        id!!,
        name!!,
        isEnabled,
        email!!
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserEn

        return id != null && id == other.id
    }

    override fun hashCode(): Int = 25762072

}

