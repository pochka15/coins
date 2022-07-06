package pw.coins.security.oauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.core.oauth.OAuth10aService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView


@RestController
@RequestMapping("oauth")
@Tag(name = "Oauth")
class UsosController(
    val oAuthService: OAuth10aService,
    val usosTokenService: UsosTokenService
) {
    @GetMapping("usos")
    fun authorize(): RedirectView {
        val requestToken = oAuthService.requestToken
        val authUrl = oAuthService.getAuthorizationUrl(requestToken)
        usosTokenService.cacheRequestToken(requestToken)
        return RedirectView(authUrl)
    }

    @PostMapping("usos-callback")
    fun obtainAccessToken(@RequestBody payload: CallbackPayload): ResponseEntity<String> {
        val cachedToken = usosTokenService.getCachedToken(payload.oauthToken)
            ?: return ResponseEntity.badRequest()
                .body("Incorrect oauth token given. Couldn't obtain access token")

        val result = usosTokenService.storeAccessToken(oAuthService.getAccessToken(cachedToken, payload.oauthVerifier))
        if (!result) return ResponseEntity.internalServerError().body("Couldn't obtain an access token")
        return ResponseEntity.ok("Stored access token")
    }
}

data class CallbackPayload(
    @JsonProperty("oauth_token")
    val oauthToken: String,
    @JsonProperty("oauth_verifier")
    val oauthVerifier: String
)
