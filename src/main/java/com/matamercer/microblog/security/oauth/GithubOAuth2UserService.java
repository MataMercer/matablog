package com.matamercer.microblog.security.oauth;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.jwt.JwtUtil;
import com.matamercer.microblog.web.error.OAuth2UserRegistrationFailed;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class GithubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public GithubOAuth2UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate= new DefaultOAuth2UserService();
        val oAuth2User = delegate.loadUser(userRequest);

        val userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        val oAuthAttributes =  OAuthAttributes.ofGithub(oAuth2User.getAttributes());
        val user = saveOrFindUser(oAuthAttributes);

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        httpServletRequest.setAttribute("accessToken", jwtUtil.createAccessToken(user.getId()));
        httpServletRequest.setAttribute("refreshToken", jwtUtil.createRefreshToken(user.getId()));

        return new CustomOAuth2User(new DefaultOAuth2User(
                user.getAuthorities(),
                oAuthAttributes.getAttributes(),
                userNameAttributeName
        ));
    }

    @Transactional
    protected User saveOrFindUser(OAuthAttributes attributes){
        val optionalUser = Optional.ofNullable(userRepository.findByoAuth2IdAndAuthenticationProvider(attributes.getId(), AuthenticationProvider.GITHUB));
        if(optionalUser.isPresent()) {
            val user = optionalUser.get();
            if(user.getAuthenticationProvider() == AuthenticationProvider.GITHUB) {
                return user;
            }
            else{
                throw new OAuth2UserRegistrationFailed("Unacceptable OAuth provider");
            }
        } else {
            return userRepository.save(attributes.convertToUser());
        }
    }


}
