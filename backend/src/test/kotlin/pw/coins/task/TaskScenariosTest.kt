package pw.coins.task

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pw.coins.db.generated.tables.pojos.Room
import pw.coins.room.NewRoom
import pw.coins.room.RoomService
import pw.coins.user.UserService

@SpringBootTest
class TaskScenariosTest(
    @Autowired val roomService: RoomService,
    @Autowired val taskService: TaskService,
    @Autowired val userService: UserService,
) {

//    @Test
//    fun `create task EXPECT correct dto returned`() {
//        val task = taskService.create(
//            NewTask(
//                "Test task",
//                "Test content",
//                LocalDate.now(),
//                10,
//                createRoom().id,
//                userService.createUser("tmp").id
//            )
//        )
//
//        Assertions.assertThat(task.id).isNotNull
//        Assertions.assertThat(task.title).isEqualTo("Test task")
//        Assertions.assertThat(task.content).isEqualTo("Test content")
//        Assertions.assertThat(task.deadline).isNotNull
//        Assertions.assertThat(task.budget).isEqualTo(10)
//    }

    private fun createRoom(roomName: String = "tmp"): Room = roomService.create(NewRoom(roomName))

}