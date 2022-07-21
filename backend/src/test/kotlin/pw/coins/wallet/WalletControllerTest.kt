package pw.coins.wallet

import org.hamcrest.CoreMatchers.equalTo
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import pw.coins.db.generated.Tables
import pw.coins.db.generated.tables.pojos.Member
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.db.generated.tables.pojos.User
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.room.NewMember
import pw.coins.room.NewRoom
import pw.coins.room.RoomService
import pw.coins.security.WithMockCustomUser
import pw.coins.user.UserService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
internal class WalletControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val roomService: RoomService,
    @Autowired val userService: UserService,
    @Autowired val walletService: WalletService,
    @Autowired val dslContext: DSLContext,
) {

    @WithMockCustomUser
    @Test
    fun `crete wallet EXPECT correct amount returned`() {
        val sandbox = buildSandbox("Test", "test-email@gmail.com", 10)
        mockMvc.get("/wallet?roomId=${sandbox.room.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            content {
                jsonPath("$.coinsAmount", equalTo(sandbox.wallet.coinsAmount))
            }
        }

    }

    @AfterEach
    fun cleanup() {
        with(dslContext) {
            deleteFrom(Tables.WALLETS).execute()
            deleteFrom(Tables.MEMBERS).execute()
            deleteFrom(Tables.ROOMS).execute()
            deleteFrom(Tables.USERS).execute()
        }
    }

    private fun buildSandbox(roomName: String, userEmail: String, coinsAmount: Int): Sandbox {
        val room = roomService.create(NewRoom(roomName))
        val user = userService.getUser(userEmail)!!
        val member = roomService.addMember(NewMember(user.id.toString(), room.id.toString()))
        val wallet = walletService.createWallet(NewWallet(coinsAmount, member.id.toString()))
        return Sandbox(user, room, member, wallet)
    }
}

data class Sandbox(
    val user: User,
    val room: Room,
    val member: Member,
    val wallet: Wallet,
)