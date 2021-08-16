package com.matamercer.microblog.security;

import com.matamercer.microblog.security.jwt.JwtConfig;
import com.matamercer.microblog.security.jwt.JwtTokenVerifier;
import com.matamercer.microblog.security.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.matamercer.microblog.security.jwt.JwtUtil;
import com.matamercer.microblog.services.UserService;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final JwtUtil jwtUtil;


    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, UserService userService, SecretKey secretKey, JwtConfig jwtConfig,  JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var jwtUsernameAndPasswordAuthenticationFilter = new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, jwtUtil);
        jwtUsernameAndPasswordAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");
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
                .addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig, jwtUtil), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/api/user/*",
                        "/api/v1/auth/login",
                        "/api/v1/auth/currentuser",
                        "index",
                        "/users/*",
                        "/register",
                        "/registerSuccess",
                        "/registerOAuth2Failure",
                        "/registration/confirm*",
                        "/baduser",
                        "/dist/*",
                        "/stylesheets/*",
                        "/img/*",
                        "/profile/*",
                        "/posts/*",
                        "/login/**",
                        "/api/user/refreshtoken",
                        "/error")
                .permitAll().anyRequest().authenticated()
                ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }





}
