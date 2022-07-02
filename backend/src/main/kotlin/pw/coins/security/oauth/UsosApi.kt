package pw.coins.security.oauth

import com.github.scribejava.core.builder.api.DefaultApi10a

class UsosApi : DefaultApi10a() {
    override fun getRequestTokenEndpoint(): String {
        return "https://apps.usos.pw.edu.pl/services/oauth/request_token"
    }

    override fun getAccessTokenEndpoint(): String {
        return "https://apps.usos.pw.edu.pl/services/oauth/access_token"
    }

    override fun getAuthorizationBaseUrl(): String {
        return "https://apps.usos.pw.edu.pl/services/oauth/authorize"
    }
}