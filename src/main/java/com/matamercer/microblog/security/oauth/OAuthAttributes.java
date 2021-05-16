package com.matamercer.microblog.security.oauth;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.web.error.OAuth2UserRegistrationFailed;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String username;
    private String email;
    private String id;

    public OAuthAttributes(Map<String, Object> attributes, String id, String username, String email) {
        this.attributes = attributes;
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public static OAuthAttributes ofGithub(Map<String, Object> attributes) {
        if (attributes.get("email") == null) {
            throw new OAuth2UserRegistrationFailed("Email is not public");
        }
        return new OAuthAttributes(attributes, attributes.get("id").toString(), attributes.get("login").toString(), attributes.get("email").toString());
    }

    public User convertToUser() {
        User newUser = new User(
                email,
                username,
                null,
                UserRole.USER,
                true,
                true,
                true,
                false,
                AuthenticationProvider.GITHUB
        );
        newUser.setOAuth2Id(id);
        return newUser;
    }

}
