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
import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public OAuth2LoginSuccessHandler(UserService userService, UserRepository userRepository){
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication instanceof OAuth2AuthenticationToken) {
            Optional<User> user = Optional.ofNullable(getUserFromOAuth2AuthenticationToken((OAuth2AuthenticationToken) authentication));
            if(!user.isPresent()){
                User newUser = initializeNewOAuth2User((OAuth2AuthenticationToken) authentication);
                try {
                    userService.createUser(newUser, UserRole.USER);
                }
                catch(Exception ex){
                    logoutUser(request);
                    response.sendRedirect("/registerOAuth2Failure");
                }
            }
            else{
                updateUsernameOnUser(user.get(), (OAuth2AuthenticationToken) authentication);
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void logoutUser(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        SecurityContextHolder.clearContext();
    }

    private AuthenticationProvider getAuthenticationProvider(OAuth2AuthenticationToken oAuth2AuthenticationToken){
       return AuthenticationProvider.valueOf(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().toUpperCase(Locale.ROOT));
    }

    private User getUserFromOAuth2AuthenticationToken(OAuth2AuthenticationToken oAuth2AuthenticationToken){
        CustomOAuth2User customoAuth2User = (CustomOAuth2User) oAuth2AuthenticationToken.getPrincipal();
        String id = customoAuth2User.getId();
        AuthenticationProvider authenticationProvider = getAuthenticationProvider(oAuth2AuthenticationToken);
        return userRepository.findByoAuth2IdAndAuthenticationProvider(id, authenticationProvider);
    }

    private User initializeNewOAuth2User(OAuth2AuthenticationToken oAuth2AuthenticationToken){
        CustomOAuth2User customoAuth2User = (CustomOAuth2User) oAuth2AuthenticationToken.getPrincipal();
        User registeredUser = new User(
                customoAuth2User.getEmail(),
                customoAuth2User.getUsername(),
                null,
                true,
                true,
                true,
                false,
                getAuthenticationProvider(oAuth2AuthenticationToken)
        );
        registeredUser.setOAuth2Id(customoAuth2User.getId());
        return registeredUser;
    }

    private void updateUsernameOnUser(User user, OAuth2AuthenticationToken oAuth2AuthenticationToken){
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) oAuth2AuthenticationToken.getPrincipal();
        String currentOAuth2Username = customOAuth2User.getUsername();
        if(!user.getUsername().equals(currentOAuth2Username)) {
            user.setUsername(currentOAuth2Username);
            userRepository.save(user);
        }
    }
}
