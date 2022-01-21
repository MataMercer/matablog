package com.matamercer.microblog.security;

import com.matamercer.microblog.security.authentication.JwtConfig;
import com.matamercer.microblog.security.authentication.JwtTokenVerifier;
import com.matamercer.microblog.security.authentication.JwtUsernameAndPasswordAuthenticationFilter;
import com.matamercer.microblog.security.authentication.JwtUtil;
import com.matamercer.microblog.security.authorization.UserAuthority;
import com.matamercer.microblog.services.UserService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .antMatchers("/api/v1/post/create").hasAuthority(UserAuthority.POST_CREATE_NEW.toString())
                .antMatchers("/api/v1/post/reply").hasAuthority(UserAuthority.POST_CREATE_COMMENT.toString())
                .antMatchers(HttpMethod.POST, "/api/v1/post/*").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/post/*").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/blog/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/auth/*").permitAll()
                .anyRequest().authenticated()
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
