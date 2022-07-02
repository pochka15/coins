package pw.coins.security.oauth

import com.github.scribejava.core.builder.api.DefaultApi10a
import com.github.scribejava.core.httpclient.HttpClient
import com.github.scribejava.core.httpclient.HttpClientConfig
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.oauth.OAuth10aService
import java.io.OutputStream

class UsosAuthService(
    api: DefaultApi10a,
    apiKey: String,
    apiSecret: String,
    callback: String,
    val scope: String,
    debugStream: OutputStream,
    userAgent: String?,
    httpClientConfig: HttpClientConfig?,
    httpClient: HttpClient
) : OAuth10aService(api, apiKey, apiSecret, callback, scope, debugStream, userAgent, httpClientConfig, httpClient) {
    override fun addOAuthParams(request: OAuthRequest, tokenSecret: String) {
        request.addParameter("scopes", scope)

//        it must go after my methods because it appends the signature taking all the params
        super.addOAuthParams(request, tokenSecret)
    }
}