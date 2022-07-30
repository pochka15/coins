package pw.coins.admin

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.coins.db.generated.tables.pojos.UsosUser
import pw.coins.room.*
import pw.coins.user.UserService
import pw.coins.usos.UsosService
import pw.coins.wallet.NewWallet
import pw.coins.wallet.WalletService
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin")
class AdminController(
    val roomService: RoomService,
    val userService: UserService,
    val usosService: UsosService,
    val walletService: WalletService,
) {
    /**
     * Create a room with members and their wallets. It also creates all the necessary users which
     * don't exist in the database
     */
    @PostMapping("/room")
    fun createRoom(@RequestBody @Valid payload: NewRoomPayload): RoomData {
        val room = roomService.create(NewRoom(payload.name))
        val users = usosService.getUsosUsersByIds(payload.participants.map { it.id })
        val ids = users.map { it.id }.toSet()

//        For each new usos user: create a default user 
        val newUsers = payload.participants
            .filter { !ids.contains(it.id) }
            .toSet()
            .map {
                val user = userService.createUser("${it.firstName} ${it.lastName}", "")
                UsosUser(it.id, it.firstName, it.lastName, null, user.id)
            }

        usosService.createUsosUsers(newUsers)

        val members = roomService.addMembers(
            (users + newUsers).map { NewMember(it.userId, room.id) }
        )

        walletService.createWallets(members.map { NewWallet(payload.initialCoinsAmount, it.id) })
        return room.toData()
    }
}

data class Participant(
    @field:NotEmpty(message = "Participant id cannot be empty")
    val id: String,

    @field:NotEmpty(message = "Participant firstName cannot be empty")
    val firstName: String,

    @field:NotEmpty(message = "Participant lastName cannot be empty")
    val lastName: String,
)

data class NewRoomPayload(
    @field:NotEmpty(message = "Name should not be empty")
    @field:Size(max = 20, message = "Name length should be <= 20")
    val name: String,

    val participants: List<Participant>,

    @field:Min(0) @field:Max(1000_000, message = "Too big initial amount")
    val initialCoinsAmount: Int
)
