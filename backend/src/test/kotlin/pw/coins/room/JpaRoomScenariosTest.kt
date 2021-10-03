package pw.coins.room

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import pw.coins.room.member.MemberRe
import pw.coins.room.member.tmpMemberEn
import pw.coins.security.EncodersConfig
import pw.coins.user.UserEn
import pw.coins.user.UserRe
import pw.coins.user.userEn

@DataJpaTest
@Import(EncodersConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class JpaRoomScenariosTest(
    @Autowired val roomRe: RoomRe,
    @Autowired val userRe: UserRe,
    @Autowired val memberRe: MemberRe,
) {

    @Test
    fun `remove a room member EXPECT associated user is not removed`() {
        val room = roomRe.save(RoomEn(name = "tmp"))
        val user = userRe.save(userEn())
        val member = memberRe.saveAndFlush(tmpMemberEn(user, room))

        memberRe.delete(member)
        memberRe.flush()

        userRe.findById(user.id!!) shouldNotBe null
    }

    @Test
    fun `remove room member EXPECT room has a correct amount of members`() {
        val membersAmount = 5
        val room = roomRe.save(RoomEn(name = "tmp"))
        val users = Array(membersAmount) { userRe.save(userEn("$it")) }
        repeat(membersAmount) { memberRe.save(tmpMemberEn(users[it], room)) }
        memberRe.flush()

        memberRe.delete(memberRe.fetchRoomMembers(room)[0])

        memberRe.fetchRoomMembers(room).size shouldBe membersAmount - 1
    }

    @Test
    fun `remove user by id EXPECT all the associated room members are be removed`() {
        val rooms = saveAndFlushRooms("room1", "room2", "room3")

        var user = UserEn(name = "u")
        for (room in rooms) user.associatedMembers.add(tmpMemberEn(user, room))
        user = userRe.saveAndFlush(user)

        userRe.delete(user)

        for (room in rooms) memberRe.fetchRoomMembers(room).size shouldBe 0
    }

    @Test
    fun `DataIntegrityViolationException remove user by id EXPECT all the associated room members are be removed`() {
        val rooms = saveAndFlushRooms("room1", "room2", "room3")
        val user = userRe.saveAndFlush(UserEn(name = "u"))

        for (room in rooms) memberRe.save(tmpMemberEn(user, room))
        memberRe.flush()

        shouldThrow<DataIntegrityViolationException> {
//        TODO(@pochka15): why doesn't it delete children then a parent in a cascade way???
            userRe.deleteById(user.id!!)
            userRe.flush()
        }
//        Assertion which should work if constraint violation gets fixed
//        for (room in rooms) fetchRoomMembers.fetchRoomMembers(room).size shouldBe 0
    }

    @Test
    fun `cascade add room members EXPECT each room has a member`() {
        val rooms = saveAndFlushRooms("room1", "room2", "room3")

        val userEn = UserEn(name = "u", associatedMembers = mutableListOf())
        for (room in rooms) userEn.associatedMembers.add(tmpMemberEn(userEn, room))

        userRe.saveAndFlush(userEn)

        for (room in rooms) memberRe.fetchRoomMembers(room).size shouldBe 1
    }

    private fun saveAndFlushRooms(vararg roomNames: String): List<RoomEn> {
        val rooms = roomNames.map { roomRe.save(RoomEn(name = it)) }
        roomRe.flush()
        return rooms
    }
}