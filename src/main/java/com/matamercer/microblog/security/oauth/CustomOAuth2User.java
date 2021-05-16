package com.matamercer.microblog.security.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oAuth2User;

    public CustomOAuth2User(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return this.getUsername();
    }

    public String getUsername() {
        return oAuth2User.getAttribute("login");
    }

    public String getOAuth2Id() {
        return Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
    }

    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }


}
