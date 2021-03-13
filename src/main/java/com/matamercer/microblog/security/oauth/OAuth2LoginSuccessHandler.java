package com.matamercer.microblog.security.oauth;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.web.error.OAuth2UserRegistrationFailed;
import com.matamercer.microblog.web.error.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        //if using oauth
        if(authentication instanceof  OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            String id = oAuth2User.getId();
            AuthenticationProvider authenticationProvider = AuthenticationProvider.valueOf(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().toUpperCase(Locale.ROOT));
            String email = oAuth2User.getEmail();
            String username = oAuth2User.getUsername();

            //We have to use id to identify the oauth user because the username and email can be changed between logins.
            //A combination of id (which is usually incremental i.e. 0, 1, .. 100) and authentication provider assures uniqueness.
            User user = userRepository.findByoAuth2IdAndAuthenticationProvider(id, authenticationProvider);

            //if OAuth2 User does not exist as a user in DB yet, create a brand new user.
            if(user == null){
                User registeredUser = new User(
                        email,
                        username,
                        null,
                        true,
                        true,
                        true,
                        false,
                        authenticationProvider
                );
                registeredUser.setOAuth2Id(id);
                try {
                    userService.createUser(registeredUser, UserRole.USER);
                }
                catch(Exception ex){
//                    request.getSession()
//                            .setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, ex.getMessage());
//                    request.setAttribute("exception", ex);

                    HttpSession session = request.getSession();
                    session.invalidate();

                    SecurityContextHolder.clearContext();
                    response.sendRedirect("/registerOAuth2Failure");

//                    throw new OAuth2UserRegistrationFailed("OAuth2 registration failed");
                }
            }
            //if not, update the user with the most up to date user name. This is because the username is required for fetching users.
            else{
                if(!user.getUsername().equals(username)) {
                    user.setUsername(username);
                    userRepository.save(user);
                }
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
