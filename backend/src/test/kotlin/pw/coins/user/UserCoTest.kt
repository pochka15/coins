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
import pw.coins.user.dtos.CreateUserPayload

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UserCoTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mapper: ObjectMapper,
) {

    @Test
    fun `create user EXPECT correct dto`() {
        val payload = CreateUserPayload("tmp")
        mockMvc.jsonPost("/user") {
            content = mapper.writeValueAsString(payload)
        }.andExpectOkJson {
            content {
                jsonPath("$.id", notNullValue())
                jsonPath("$.name", `is`("tmp"))
                jsonPath("$.isEnabled", `is`(true))
            }
        }
    }
}