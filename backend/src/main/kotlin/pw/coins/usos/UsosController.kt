package pw.coins.usos

import com.github.scribejava.core.model.OAuth1AccessToken
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import pw.coins.db.generated.tables.pojos.User
import pw.coins.security.PrincipalContext
import pw.coins.security.oauth.UsosTokenService
import pw.coins.usos.model.ApiCourseEdition

@RestController
@RequestMapping("usos")
@Tag(name = "Usos")
class UsosController(val usosApi: UsosApi, val tokenService: UsosTokenService) {
    @GetMapping("course_edition")
    fun getCourseEdition(
        @RequestParam courseId: String,
        @RequestParam semester: String,
        @PrincipalContext user: User
    ): CourseEditionData {
        val usosToken = tokenService.getTokenByUserId(user.id)
            ?: throw ResponseStatusException(FORBIDDEN, "You don't have permissions to get a course edition")

        val token = OAuth1AccessToken(usosToken.key, usosToken.secret)
        return try {
            usosApi.getCourseEdition(token, courseId, semester).toData()
        } catch (e: UsosException) {
            throw ResponseStatusException(INTERNAL_SERVER_ERROR, e.message)
        } catch (e: ParseException) {
            throw ResponseStatusException(INTERNAL_SERVER_ERROR, e.message)
        } catch (e: UnauthorizedException) {
            throw ResponseStatusException(FORBIDDEN, "Please login again. Your authorization is expired")
        }
    }
}

data class ParticipantData(
    val id: String,
    val firstName: String,
    val lastName: String,
)

data class CourseEditionData(
    val courseId: String,
    val courseName: String,
    val participants: List<ParticipantData>,
    val lecturers: List<ParticipantData>,
)

fun ApiCourseEdition.toData(): CourseEditionData {
    val lecturers = userGroups.flatMap { it.lecturers }
        .distinct().map { ParticipantData(it.id, it.firstName, it.lastName) }

    val participants = userGroups.flatMap { it.participants }
        .distinct().map { ParticipantData(it.id, it.firstName, it.lastName) }

    return CourseEditionData(courseId, courseName.pl, participants, lecturers)
}