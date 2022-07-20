package pw.coins.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import pw.coins.security.filters.BaseAuthorizationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(
    val jwtService: JwtService,
) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(security: HttpSecurity) {
        security
            .logout().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .cors() // There exists separate CorsConfig
            .and().csrf().disable()
            .authorizeRequests()
            .antMatchers(
                HttpMethod.GET,
                "/",
                "/oauth/usos",
//                Swagger
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/v2/api-docs",
                "/v3/api-docs/**",
                "/swagger-ui/**"
            )
            .permitAll()
            .antMatchers(HttpMethod.POST, "/oauth/usos-callback").permitAll()
            .anyRequest().authenticated()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterBefore(BaseAuthorizationFilter(jwtService), BasicAuthenticationFilter::class.java)
    }
}