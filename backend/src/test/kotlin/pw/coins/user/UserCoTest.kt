package pw.coins.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import pw.coins.andExpectOkJson
import pw.coins.jsonPost
import pw.coins.user.dtos.UserCredentials

@AutoConfigureMockMvc
@SpringBootTest
internal class UserCoTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mapper: ObjectMapper,
    @Autowired val userSe: UserSe,
) {

    @Test
    fun `create user EXPECT correct dto`() {
        val credentials = tmpCredentials("user")
        mockMvc.jsonPost("/user") {
            content = mapper.writeValueAsString(credentials)
        }.andExpectOkJson {
            content {
                jsonPath("$.id", notNullValue())
                jsonPath("$.name", `is`(credentials.name))
                jsonPath("$.isEnabled", `is`(true))
                jsonPath("$.email", `is`(credentials.email))
            }
        }
    }

    private fun tmpCredentials(name: String) = UserCredentials(
        name = name,
        password = "randomPassword",
        email = "some-email@mail.ru"
    )
}