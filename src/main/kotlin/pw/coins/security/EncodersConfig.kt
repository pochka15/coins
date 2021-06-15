package pw.coins.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class EncodersConfig {
    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()
}