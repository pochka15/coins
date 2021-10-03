package pw.coins.user.wallet

import org.hibernate.Hibernate
import pw.coins.user.UserEn
import javax.persistence.*

@Entity
@Table(name = "wallets")
class WalletEn(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    var name: String,

    var coinsAmount: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    var owner: UserEn? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as WalletEn

        return id != null && id == other.id
    }

    override fun hashCode(): Int = 872770256
}