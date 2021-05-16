package com.matamercer.microblog.security;

import com.matamercer.microblog.security.jwt.JwtConfig;
import com.matamercer.microblog.security.jwt.JwtTokenVerifier;
import com.matamercer.microblog.security.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.matamercer.microblog.security.jwt.JwtUtil;
import com.matamercer.microblog.security.oauth.GithubOAuth2UserService;
import com.matamercer.microblog.security.oauth.GithubOAuthOnSuccessHandler;
import com.matamercer.microblog.services.UserService;
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
    private final GithubOAuthOnSuccessHandler githubOAuthOnSuccessHandler;
    private  final GithubOAuth2UserService githubOAuth2UserService;
    private final JwtUtil jwtUtil;


    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, UserService userService, SecretKey secretKey, JwtConfig jwtConfig, GithubOAuthOnSuccessHandler githubOAuthOnSuccessHandler, GithubOAuth2UserService githubOAuth2UserService, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.githubOAuthOnSuccessHandler = githubOAuthOnSuccessHandler;
        this.githubOAuth2UserService = githubOAuth2UserService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, jwtUtil))
                .addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/api/user/*",
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
                        "/oauth2/authorization/**",
                        "/oauth2/**",
                        "/login/**",
                        "/api/user/refreshtoken",
                        "/error")
                .permitAll().anyRequest().authenticated()
                .and()
                .oauth2Login()
                .successHandler(githubOAuthOnSuccessHandler)
                .userInfoEndpoint()
                .userService(githubOAuth2UserService)
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
