package com.matamercer.microblog.security

import com.matamercer.microblog.security.authentication.JwtConfig
import com.matamercer.microblog.security.authentication.JwtTokenVerifier
import com.matamercer.microblog.security.authentication.JwtUsernameAndPasswordAuthenticationFilter
import com.matamercer.microblog.security.authentication.JwtUtil
import com.matamercer.microblog.security.authorization.UserAuthority
import com.matamercer.microblog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import javax.crypto.SecretKey

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ApplicationSecurityConfig @Autowired constructor(
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService,
    private val secretKey: SecretKey,
    private val jwtConfig: JwtConfig,
    private val jwtUtil: JwtUtil
) : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        val jwtUsernameAndPasswordAuthenticationFilter =
            JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, jwtUtil)
        jwtUsernameAndPasswordAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/login")
        http
            .cors()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .formLogin().disable()
            .httpBasic().disable()
            .logout()
            .and()
            .addFilter(jwtUsernameAndPasswordAuthenticationFilter)
            .addFilterAfter(
                JwtTokenVerifier(secretKey, jwtConfig, jwtUtil),
                JwtUsernameAndPasswordAuthenticationFilter::class.java
            )
            .authorizeRequests()
            .antMatchers("/api/v1/post/create").hasAuthority(UserAuthority.POST_CREATE_NEW.toString())
            .antMatchers("/api/v1/post/reply").hasAuthority(UserAuthority.POST_CREATE_COMMENT.toString())
            .antMatchers(HttpMethod.POST, "/api/v1/post/*").authenticated()
            .antMatchers(HttpMethod.GET, "/api/v1/post/*").permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/blog/*").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/*").permitAll()
            .anyRequest().authenticated()
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(daoAuthenticationProvider())
    }

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder)
        provider.setUserDetailsService(userService)
        return provider
    }
}