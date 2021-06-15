package pw.coins.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(security: HttpSecurity) {
        security.authorizeRequests()
            //                .antMatchers(HttpMethod.GET,
            //                             "/user",
            //                             "/comments-websocket/**",
            ////                             Swagger
            //                             "/configuration/ui",
            //                             "/configuration/security",
            //                             "/swagger-ui.html",
            //                             "/swagger-ui/**",
            //                             "/swagger-resources/**",
            //                             "/swagger-ui.html",
            //                             "/webjars/**",
            //                             "/v2/api-docs",
            //                             "/v3/api-docs/**",
            //                             "/swagger-ui/**")
            //                .permitAll()
            //                .antMatchers(HttpMethod.POST, "/register", "/login").permitAll()
            //                .antMatchers("/user/admin/**").hasAuthority("ADMIN")
            //                .anyRequest().authenticated()
            .anyRequest().permitAll()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().exceptionHandling()
            .and().cors()
            .and().csrf().disable()
    }
}