package pw.coins.security.filters

import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import pw.coins.security.JwtService
import pw.coins.security.model.CustomUserDetails
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class BaseAuthorizationFilter(private val jwtService: JwtService) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader(AUTHORIZATION)
        if (header != null && header.startsWith("Bearer ")) {
            val token = try {
                jwtService.parseToken(header.substringAfter("Bearer "))
            } catch (e: Exception) {
                response.contentType = APPLICATION_JSON_VALUE
                response.status = HttpStatus.BAD_REQUEST.value()
                ObjectMapper().writeValue(
                    response.outputStream,
                    mapOf("message" to "Error occurred when trying to parse JWT token")
                )
                return
            }

            val authorities = token.extractAuthorities()
            val principal = CustomUserDetails(token, authorities)

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(principal, null, authorities)
        }
        filterChain.doFilter(request, response)
    }

    private fun CustomUserDetails(
        token: DecodedJWT,
        authorities: MutableList<SimpleGrantedAuthority>
    ) = CustomUserDetails(
        token.subject,
        "",
        authorities,
        UUID.fromString(token.getClaim("userId").asString()),
        token.getClaim("email").asString()
    )

    private fun DecodedJWT.extractAuthorities(): MutableList<SimpleGrantedAuthority> {
        return getClaim("roles").asArray(String::class.java)
            .map { SimpleGrantedAuthority(it) }
            .toMutableList()
    }
}