package pw.coins.task

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.room.NewRoom
import pw.coins.room.RoomSe
import pw.coins.task.dtos.NewTask
import pw.coins.user.UserSe
import java.time.LocalDate

@SpringBootTest
class TaskScenariosTest(
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
                userSe.createUser("tmp").id
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

}