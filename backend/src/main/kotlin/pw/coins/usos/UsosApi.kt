package pw.coins.usos

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import org.springframework.stereotype.Component
import pw.coins.sys.logger
import pw.coins.usos.model.ApiCourseEdition
import pw.coins.usos.model.ApiUsosUser

private const val api = "https://apps.usos.pw.edu.pl/services"

// USOS API ver. 6.8.0.0-3, 0cdab6f1, dirty (2022-09-16)
@Component
class UsosApi(val oAuthService: OAuth10aService) {
    val log = logger<UsosApi>()

    fun getUser(token: OAuth1AccessToken): ApiUsosUser {
        val request = OAuthRequest(
            Verb.GET,
            "$api/users/user?fields=id|first_name|last_name|email"
        )
        oAuthService.signRequest(token, request)
        val body = oAuthService.execute(request).body
        return jacksonObjectMapper().readValue(body, ApiUsosUser::class.java)
    }

    fun getCourseEdition(token: OAuth1AccessToken, courseId: String, termId: String): ApiCourseEdition {
        val request = OAuthRequest(
            Verb.POST,
            "https://apps.usos.pw.edu.pl/services/courses/course_edition",
        )
        with(request) {
            addBodyParameter("course_id", courseId)
            addBodyParameter("term_id", termId)
            addBodyParameter("format", "json")
        }
        oAuthService.signRequest(token, request)
        val response = oAuthService.execute(request)
        val body = response.body

        log.info("Response from USOS: code: ${response.code}, message: ${response.message}, body: $body")

        if (response.isSuccessful) {
            return try {
                jacksonObjectMapper().readValue(body, ApiCourseEdition::class.java)
            } catch (e: Exception) {
                throw ParseException("Couldn't parse the course edition", e)
            }
        }

        if (response.code == 401) throw UnauthorizedException()

        val prefix = "Error ${response.code}."
        val node = ObjectMapper().readTree(body).path("message")
        val suffix = if (node.isMissingNode) {
            "Unknown error occurred when getting course edition"
        } else node.asText()
        throw UsosException("$prefix $suffix")
    }
}

class UsosException(message: String?) : RuntimeException(message)
class UnauthorizedException() : RuntimeException("Your authorization is expired. Please login again")
class ParseException(message: String?, cause: Throwable?) : RuntimeException(message, cause)