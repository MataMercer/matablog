package com.matamercer.microblog.security.oauth;

import com.matamercer.microblog.utilities.EnvironmentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class GithubOAuthOnSuccessHandler implements AuthenticationSuccessHandler {

    private final EnvironmentUtil environmentUtil;

    @Autowired
    public GithubOAuthOnSuccessHandler(EnvironmentUtil environmentUtil) {
        this.environmentUtil = environmentUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirUrl = String.format("%s?accessToken=%s&refreshToken=%s", environmentUtil.getServerUrl(), request.getAttribute("accessToken"), request.getAttribute("refreshToken"));
        response.sendRedirect(redirUrl);
    }
}
