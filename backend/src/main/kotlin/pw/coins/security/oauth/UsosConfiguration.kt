package pw.coins.security.oauth

import com.github.scribejava.core.oauth.OAuth10aService
import com.github.scribejava.httpclient.okhttp.OkHttpHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UsosConfiguration(
    @Value("\${usos.apiKey}") val apiKey: String,
    @Value("\${usos.apiSecret}") val apiSecret: String,
) {
    @Bean
    fun oauthService(): OAuth10aService {
        return UsosAuthService(
            api = UsosApi(),
            apiKey = apiKey,
            apiSecret = apiSecret,
            callback = "http://localhost:3000/oauth/usos-callback",
            scope = "cards",
            debugStream = System.out,
            userAgent = null,
            httpClientConfig = null,
            httpClient = OkHttpHttpClient(),
        )
    }
}