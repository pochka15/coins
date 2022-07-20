package pw.coins.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import pw.coins.db.generated.Tables
import pw.coins.security.WithMockCustomUser

@AutoConfigureMockMvc
@SpringBootTest
@WithMockCustomUser
internal class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mapper: ObjectMapper,
    @Autowired val dslContext: DSLContext,
) {

    @Test
    fun `create a user EXPECT correct dto returned`() {
        val payload = CreateUserPayload("tmp")
        val post = mockMvc.post("/user", arrayOf<Any?>()) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(payload)
        }
        post.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.id", notNullValue())
                jsonPath("$.name", `is`("tmp"))
            }
        }
    }

    @AfterEach
    fun cleanup() {
        dslContext
            .deleteFrom(Tables.USERS)
            .execute()
    }
}