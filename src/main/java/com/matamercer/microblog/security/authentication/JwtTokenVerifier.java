package com.matamercer.microblog.security.authentication;

import com.google.common.base.Strings;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.security.authorization.UserRole;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenVerifier extends OncePerRequestFilter {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final JwtUtil jwtUtil;

    public JwtTokenVerifier(SecretKey secretKey, JwtConfig jwtConfig, JwtUtil jwtUtil){
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());

        if(Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");
        try{
            val body = jwtUtil.extractAllClaims(token);

            val userId =  Long.parseLong((String)body.get("userId"));
            val username = (String) body.get("username");
            val userRole = UserRole.valueOf((String)body.get("userRole"));
            val activeBlogId = Long.parseLong((String) body.get("activeBlogId"));
            val simpleGrantedAuthorities = userRole.getGrantedAuthorities();

            val user = new User();
            user.setId(userId);
            user.setUsername(username);
            user.setRole(userRole);
            Blog activeBlog = new Blog();
            activeBlog.setId(activeBlogId);
            user.setActiveBlog(activeBlog);


            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    simpleGrantedAuthorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch(AuthenticationException e){
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }
}
