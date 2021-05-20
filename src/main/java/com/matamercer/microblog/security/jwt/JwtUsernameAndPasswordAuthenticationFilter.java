package com.matamercer.microblog.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.services.UserService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final JwtUtil jwtUtil;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig, JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        try {
            UsernameAndPasswordAuthenticationRequest usernameAndPasswordAuthenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
               usernameAndPasswordAuthenticationRequest.getUsername(),
                    usernameAndPasswordAuthenticationRequest.getPassword()
            );
            return authenticationManager.authenticate(authentication);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String accessToken = jwtUtil.createAccessToken(authResult.getName());
        String refreshToken = jwtUtil.createRefreshToken(authResult.getName());

        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + accessToken);
        response.addHeader("refreshToken", refreshToken);
    }
}
