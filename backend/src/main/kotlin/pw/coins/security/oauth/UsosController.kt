package pw.coins.security.oauth

import com.github.scribejava.core.oauth.OAuth10aService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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

    @GetMapping("usos-callback")
    fun obtainAccessToken(
        @RequestParam(name = "oauth_token") oauthToken: String,
        @RequestParam(name = "oauth_verifier") oauthVerifier: String
    ): ResponseEntity<String> {
        val cachedToken = usosTokenService.getCachedToken(oauthToken)
            ?: return ResponseEntity.badRequest()
                .body("Incorrect oauth token given. Couldn't obtain access token")

        val result = usosTokenService.storeAccessToken(oAuthService.getAccessToken(cachedToken, oauthVerifier))
        if (!result) return ResponseEntity.internalServerError().body("Couldn't obtain an access token")
        return ResponseEntity.ok("Stored access token")
    }
}