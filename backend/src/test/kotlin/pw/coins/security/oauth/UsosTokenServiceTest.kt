package pw.coins.security.oauth

import com.github.scribejava.core.model.OAuth1AccessToken
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class UsosTokenServiceTest(
    @Autowired val usosTokenService: UsosTokenService
) {
    @Test
    fun `store token EXPECT created date is not empty`() {
        usosTokenService.storeAccessToken(OAuth1AccessToken("key", "secret"))
        val token = usosTokenService.getTokenByKey("key")
        Assertions.assertThat(token).isNotNull
        println(token)
    }
}