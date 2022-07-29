package pw.coins.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import pw.coins.security.filters.BaseAuthorizationFilter


@Configuration
class SecurityConfig(val jwtService: JwtService) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .logout().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .cors() // There exists separate CorsConfig
            .and().csrf().disable()
            .authorizeRequests()
            .antMatchers(
                GET,
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
            .antMatchers(POST, "/oauth/usos-callback").permitAll()
            .antMatchers("/tasks/**").hasAuthority("USER")
            .antMatchers("/rooms/**").hasAuthority("USER")
            .antMatchers("/wallets/**").hasAuthority("USER")
            .antMatchers(GET, "/users/**").hasAuthority("USER")
            .antMatchers(POST, "/users").hasAuthority("ADMIN")
            .antMatchers(DELETE, "/users/**").hasAuthority("ADMIN")
            .antMatchers(POST, "/admin/**").hasAuthority("ADMIN")
            .anyRequest().denyAll()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterBefore(BaseAuthorizationFilter(jwtService), BasicAuthenticationFilter::class.java)
            .build()
    }
}