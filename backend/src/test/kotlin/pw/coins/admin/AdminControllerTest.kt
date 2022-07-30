package pw.coins.admin

import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.test.web.servlet.post
import pw.coins.db.generated.Tables.*
import pw.coins.db.generated.tables.pojos.Wallet
import pw.coins.security.WithMockAdmin
import pw.coins.security.WithMockCustomUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
internal class AdminControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val dslContext: DSLContext,
) {
    @Test
    @WithMockCustomUser
    fun `create a room as a user EXPECT no permissions`() {
        mockMvc.post("/admin/room") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isForbidden() }
        }
    }

    @Test
    @WithMockAdmin
    fun `create a room EXPECT room and members and wallets are created correctly`() {
        mockMvc.post("/admin/room") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = /* language=JSON */ """
               {
                  "name": "Test room",
                  "members": [
                    {
                      "id": "id 1",
                      "first_name": "Name",
                      "last_name": "1"
                    },
                    {
                      "id": "id 2",
                      "first_name": "Name",
                      "last_name": "2"
                    },
                    {
                      "id": "id 3",
                      "first_name": "Name",
                      "last_name": "3"
                    }
                  ],
                  "initialCoinsAmount": 100
               } 
            """.trimIndent()
        }.andExpect {
            content { jsonPath("$.name", equalTo("Test room")) }
        }

        val membersAmount = dslContext.fetchCount(MEMBERS)
        val usosUsersAmount = dslContext.fetchCount(USOS_USERS)
        val wallets = dslContext.fetch(WALLETS).into(Wallet::class.java)

        assertThat(membersAmount).isEqualTo(3)
        assertThat(usosUsersAmount).isEqualTo(3)
        assertThat(wallets.map { it.coinsAmount }).allMatch { it == 100 }
    }

    @Test
    @WithMockAdmin
    fun `create a room with members containing same ids EXPECT no errors`() {
        mockMvc.post("/admin") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = /* language=JSON */ """
               {
                  "name": "Test room",
                  "members": [
                    {
                      "id": "same id",
                      "first_name": "Name",
                      "last_name": "1"
                    },
                    {
                      "id": "same id",
                      "first_name": "Name",
                      "last_name": "1"
                    },
                    {
                      "id": "id 3",
                      "first_name": "Name",
                      "last_name": "3"
                    }
                  ],
                  "initialCoinsAmount": 100
               } 
            """.trimIndent()
        }.andExpect { status { is2xxSuccessful() } }

        assertThat(dslContext.fetchCount(MEMBERS)).isEqualTo(2)
        assertThat(dslContext.fetchCount(USOS_USERS)).isEqualTo(2)
        assertThat(dslContext.fetchCount(WALLETS)).isEqualTo(2)
    }


    @AfterEach
    fun cleanup() {
        with(dslContext) {
            deleteFrom(WALLETS).execute()
            deleteFrom(USOS_USERS).execute()
            deleteFrom(MEMBERS).execute()
            deleteFrom(ROOMS).execute()
            deleteFrom(USERS).execute()
        }
    }
}