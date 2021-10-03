package pw.coins.user.wallet

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import pw.coins.room.RoomEn
import pw.coins.room.RoomRe
import pw.coins.room.member.MemberRe
import pw.coins.room.member.tmpMemberEn
import pw.coins.security.EncodersConfig
import pw.coins.user.UserRe
import pw.coins.user.userEn

@DataJpaTest
@Import(EncodersConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class WalletScenariosTest(
    @Autowired val roomRe: RoomRe,
    @Autowired val userRe: UserRe,
    @Autowired val memberRe: MemberRe,
    @Autowired val walletRe: WalletRe,
) {

    /**
    - Create room with 3 members. For each member create a wallet.
    - Send money from the temporary wallet to each of the created wallets

    EXPECT:

    - Each wallet has a correct amount of money
     */
//    @Test
//    internal fun `scenario 1`() {
//        val wallet = walletRe.save(WalletEn(
//            name = "tmp",
//            owner = userRe.save(userEn(name = "tmpUser")),
//            coinsAmount = 10))
//        val room = roomRe.save(RoomEn(name = "tmp"))
//        val users = (1..3).map { userRe.save(userEn(name = "user $it")) }
//        val members = users.map { memberRe.save(tmpMemberEn(it, room)) }
//        memberRe.flush()
//        
////        TODO(@pochka15): how to send money? It's not a Jpa test!
//        
//    }
}