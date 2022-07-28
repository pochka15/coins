package pw.coins.security.oauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.core.oauth.OAuth10aService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.view.RedirectView
import pw.coins.room.GLOBAL_ROOM_ID
import pw.coins.room.NewMember
import pw.coins.room.RoomService
import pw.coins.security.JwtService
import pw.coins.user.UserService
import pw.coins.usos.UsosApi
import pw.coins.usos.UsosService
import pw.coins.wallet.NewWallet
import pw.coins.wallet.WalletService
import java.util.*


@RestController
@RequestMapping("oauth")
@Tag(name = "Oauth")
class UsosController(
    val oAuthService: OAuth10aService,
    val usosTokenService: UsosTokenService,
    val usosApi: UsosApi,
    val usosService: UsosService,
    val userService: UserService,
    val jwtService: JwtService,
    val roomService: RoomService,
    val walletService: WalletService,
) {
    @GetMapping("usos")
    fun authorize(): RedirectView {
        val requestToken = oAuthService.requestToken
        val authUrl = oAuthService.getAuthorizationUrl(requestToken)
        usosTokenService.cacheRequestToken(requestToken)
        return RedirectView(authUrl)
    }

    @PostMapping("usos-callback")
    fun obtainAccessToken(@RequestBody payload: CallbackPayload): JwtData {
        val cachedToken = usosTokenService.getCachedToken(payload.oauthToken)
            ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Incorrect oauth token given. Couldn't obtain access token"
            )

        val token = oAuthService.getAccessToken(cachedToken, payload.oauthVerifier)
            ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Couldn't obtain access token"
            )

//        Next we run 'effects': insert necessary db records and finally store the token 
        val apiUser = usosApi.getUser(token)
        val usosUser = usosService.getUsosUserById(apiUser.id)
        val user = if (usosUser == null) {
            val user = userService.createUser("${apiUser.firstName} ${apiUser.lastName}", apiUser.email)
            val member = roomService.addMember(NewMember(user.id, UUID.fromString(GLOBAL_ROOM_ID)))
            usosService.createUsosUser(apiUser, user.id)
            walletService.createWallet(NewWallet(100, member.id))
            user
        } else userService.getUserById(usosUser.userId)!!

        usosTokenService.storeAccessToken(token, user.id)

        return JwtData(jwtService.buildToken(user))
    }
}

data class CallbackPayload(
    @JsonProperty("oauth_token")
    val oauthToken: String,
    @JsonProperty("oauth_verifier")
    val oauthVerifier: String
)

data class JwtData(
    val jwtToken: String
)
