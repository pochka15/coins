package pw.coins.task

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import pw.coins.andExpectOkJson
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.jsonPost
import pw.coins.room.RoomSe
import pw.coins.room.dtos.NewRoom
import pw.coins.task.dtos.NewTask
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
internal class TaskCoTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mapper: ObjectMapper,
    @Autowired val roomSe: RoomSe
) {

    @Test
    fun `create task EXPECT correct dto returned`() {
        postTask(
            NewTask(
                "Test task",
                "Test content",
                LocalDate.now(),
                10,
                createRoom().id
            )
        ).andExpectOkJson {
            content {
                jsonPath("$.id", CoreMatchers.notNullValue())
                jsonPath("$.title", CoreMatchers.equalTo("Test task"))
                jsonPath("$.content", CoreMatchers.equalTo("Test content"))
                jsonPath("$.deadline", CoreMatchers.notNullValue())
                jsonPath("$.budget", CoreMatchers.equalTo(10))
                jsonPath("$.roomId", CoreMatchers.notNullValue())
            }
        }
    }

    private fun createRoom(roomName: String = "tmp"): Room = roomSe.create(NewRoom(roomName))

    private fun postTask(task: NewTask): ResultActionsDsl {
        return mockMvc.jsonPost(
            "/tasks/new"
        ) {
            content = mapper.writeValueAsString(task)
        }
    }
}