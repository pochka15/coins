package pw.coins.security.oauth

import com.github.scribejava.core.oauth.OAuth10aService
import com.github.scribejava.httpclient.okhttp.OkHttpHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UsosConfiguration {
    @Bean
    fun oauthService(): OAuth10aService {
        return UsosAuthService(
            api = UsosApi(),
            apiKey = "wdJbgtUSwTrRf5S328eD",
            apiSecret = "PqAr2rfUqXpm6tV6xtpAC3g3zUrPUfGuH8eMANZJ",
            callback = "http://localhost:8080/oauth/usos-callback",
            scope = "cards",
            debugStream = System.out,
            userAgent = null,
            httpClientConfig = null,
            httpClient = OkHttpHttpClient(),
        )
    }
}