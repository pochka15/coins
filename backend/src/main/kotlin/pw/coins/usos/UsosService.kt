package pw.coins.usos

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import org.springframework.stereotype.Service


@Service
class UsosService(val oAuthService: OAuth10aService) {
    fun getUser(token: OAuth1AccessToken): UsosUser {
        val request = OAuthRequest(Verb.GET, "https://apps.usos.pw.edu.pl/services/users/user?fields=id|first_name|last_name|email")
        oAuthService.signRequest(token, request)
        val body = oAuthService.execute(request).body
        return ObjectMapper().readValue(body, UsosUser::class.java)
    }
}

data class UsosUser(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("first_name")
    val firstName: String,
    
    @JsonProperty("last_name")
    val lastName: String,

    @JsonProperty("email")
    val email: String,
)