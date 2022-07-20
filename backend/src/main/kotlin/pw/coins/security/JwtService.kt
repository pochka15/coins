package pw.coins.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.pojos.User

@Service
class JwtService(
    @Value("\${jwt.secret}") val jwtSecret: String
) {
    fun buildToken(user: User): String {
        return JWT.create()
            .withSubject(user.name)
            .withClaim("userId", user.id.toString())
            .withClaim("email", user.email)
            .withClaim("roles", mutableListOf("USER"))
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    fun parseToken(value: String): DecodedJWT {
        return JWT.require(Algorithm.HMAC256(jwtSecret))
            .build()
            .verify(value)
    }
}