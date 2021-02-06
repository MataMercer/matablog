package com.matamercer.microblog.security.oauth;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.web.error.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        String username = oAuth2User.getUsername();

        if(!userService.usernameExists(username) && !userService.emailExists(email)) {
            User registeredUser = new User(
                    email,
                    username,
                    null,
                    true,
                    true,
                    true,
                    false,
                    AuthenticationProvider.GITHUB
            );
            userService.createUser(registeredUser, UserRole.USER);
        }
        else{
            User user = userRepository.findByUsername(username);
            if(user.getAuthenticationProvider() == AuthenticationProvider.LOCAL){
                throw new UserAlreadyExistsException("There is already a local account with that username.");
            }
            //else that user is a user we made for an Oauth login already. So procede as usual
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
