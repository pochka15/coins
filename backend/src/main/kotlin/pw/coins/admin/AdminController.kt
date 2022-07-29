package pw.coins.admin

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.coins.db.generated.tables.pojos.UsosUser
import pw.coins.room.*
import pw.coins.user.UserService
import pw.coins.usos.ApiUsosUser
import pw.coins.usos.UsosService
import pw.coins.wallet.NewWallet
import pw.coins.wallet.WalletService

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
    @PostMapping
    fun createRoom(@RequestBody payload: NewRoomPayload): RoomData {
        val room = roomService.create(NewRoom(payload.name))
        val users = usosService.getUsosUsersByIds(payload.members.map { it.id })
        val ids = users.map { it.id }.toSet()

//        For each new usos user: create a default user 
        val newUsers = payload.members
            .filter { !ids.contains(it.id) }
            .toSet()
            .map {
                val user = userService.createUser("${it.firstName} ${it.lastName}", it.email.orEmpty())
                UsosUser(it.id, it.firstName, it.lastName, it.email, user.id)
            }

        usosService.createUsosUsers(newUsers)

        val members = roomService.addMembers(
            (users + newUsers).map { NewMember(it.userId, room.id) }
        )

        walletService.createWallets(members.map { NewWallet(payload.initialCoinsAmount, it.id) })
        return room.toData()
    }
}

data class NewRoomPayload(
    val name: String,
    val members: List<ApiUsosUser>,
    val initialCoinsAmount: Int
)
