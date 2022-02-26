package pw.coins.task

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.db.generated.tables.pojos.User
import pw.coins.room.RoomSe
import pw.coins.room.dtos.NewRoom
import pw.coins.task.dtos.NewTask
import pw.coins.user.UserSe
import pw.coins.user.dtos.UserCredentials
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
internal class TaskScenariosTest(
    @Autowired val roomSe: RoomSe,
    @Autowired val taskSe: TaskSe,
    @Autowired val userSe: UserSe,
) {

    @Test
    fun `create task EXPECT correct dto returned`() {
        val task = taskSe.create(
            NewTask(
                "Test task",
                "Test content",
                LocalDate.now(),
                10,
                createRoom().id,
                createUser().id
            )
        )

        Assertions.assertThat(task.id).isNotNull
        Assertions.assertThat(task.title).isEqualTo("Test task")
        Assertions.assertThat(task.content).isEqualTo("Test content")
        Assertions.assertThat(task.deadline).isNotNull
        Assertions.assertThat(task.budget).isEqualTo(10)
        Assertions.assertThat(task.roomId).isNotNull
    }

    private fun createRoom(roomName: String = "tmp"): Room = roomSe.create(NewRoom(roomName))
    private fun createUser(): User = userSe.createUser(tmpCredentials())

    private fun tmpCredentials() = UserCredentials(
        name = "tmp",
        password = "randomPassword",
        email = "some-email@mail.ru"
    )
}