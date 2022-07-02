package pw.coins.room

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
internal class RoomControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mapper: ObjectMapper,
) {

    @Test
    fun `create room EXPECT correct name and id returned`() {
        mockMvc.post("/room") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(NewRoom("test-room"))
        }.andExpect {
            content {
                jsonPath("$.name", `is`("test-room"))
                jsonPath("$.id", notNullValue())
            }
        }
    }

}