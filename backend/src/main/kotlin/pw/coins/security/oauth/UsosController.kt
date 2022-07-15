package pw.coins.security.oauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.core.oauth.OAuth10aService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import pw.coins.user.UserService
import pw.coins.usos.UsosService


@RestController
@RequestMapping("oauth")
@Tag(name = "Oauth")
class UsosController(
    val oAuthService: OAuth10aService,
    val usosTokenService: UsosTokenService,
    val usosService: UsosService,
    val userService: UserService,
) {
    @GetMapping("usos")
    fun authorize(): RedirectView {
        val requestToken = oAuthService.requestToken
        val authUrl = oAuthService.getAuthorizationUrl(requestToken)
        usosTokenService.cacheRequestToken(requestToken)
        return RedirectView(authUrl)
    }

    //    TODO remove Any return type and write wrappers form habr that don't return response entity
    @PostMapping("usos-callback")
    fun obtainAccessToken(@RequestBody payload: CallbackPayload): ResponseEntity<Any> {
        val cachedToken = usosTokenService.getCachedToken(payload.oauthToken)
            ?: return ResponseEntity.badRequest()
                .body("Incorrect oauth token given. Couldn't obtain access token")

//        TODO return user data
        val token = oAuthService.getAccessToken(cachedToken, payload.oauthVerifier)
        val result = usosTokenService.storeAccessToken(token)
        if (!result) return ResponseEntity.internalServerError().body("Couldn't obtain access token")

        val usosUser = usosService.getUser(token)
        var user = userService.getUser(usosUser.email)
        if (user == null) user = userService.createUser("${usosUser.firstName} ${usosUser.lastName}", usosUser.email)
        return ResponseEntity.ok(user)
    }
}

data class CallbackPayload(
    @JsonProperty("oauth_token")
    val oauthToken: String,
    @JsonProperty("oauth_verifier")
    val oauthVerifier: String
)
