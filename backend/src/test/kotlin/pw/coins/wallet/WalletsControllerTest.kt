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
import pw.coins.room.NewMember
import pw.coins.room.NewRoom
import pw.coins.room.RoomService
import pw.coins.security.WithMockCustomUser
import pw.coins.user.UserService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
internal class WalletsControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val roomService: RoomService,
    @Autowired val userService: UserService,
    @Autowired val walletService: WalletService,
    @Autowired val dslContext: DSLContext,
) {

    @WithMockCustomUser
    @Test
    fun `create wallet EXPECT correct amount returned`() {
//        --- Sandbox
        val room = roomService.create(NewRoom("Test"))
        val user = userService.getUserByEmail("test-email@gmail.com")!!
        val member = roomService.addMember(NewMember(user.id, room.id))
        val wallet = walletService.createWallet(NewWallet(10, member.id))
//        --- End Sandbox

        mockMvc.get("/wallets?roomId=${room.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            content {
                jsonPath("$.coinsAmount", equalTo(wallet.coinsAmount))
            }
        }
    }

    @WithMockCustomUser
    @Test
    fun `create a wallet for another user EXPECT no read permissions`() {
//        --- Sandbox
        val room = roomService.create(NewRoom("Test"))

//        User 1
        val user = userService.getUserByEmail("test-email@gmail.com")!!
        roomService.addMember(NewMember(user.id, room.id))

//        User 2
        val user2 = userService.createUser("Test user 2", "test-email2@gmail.com")
        val member2 = roomService.addMember(NewMember(user2.id, room.id))
        val wallet2 = walletService.createWallet(NewWallet(10, member2.id))
//        --- End Sandbox


        mockMvc.get("/wallets/${wallet2.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isForbidden() } }
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

}