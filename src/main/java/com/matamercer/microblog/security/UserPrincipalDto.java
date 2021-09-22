package com.matamercer.microblog.security;

import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Data
public class UserPrincipalDto implements UserDetails {

    private long id;
    private String email;
    private String username;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean CredentialsNonExpired;
    private boolean enabled;
    private UserRole role;

    @Override
    public Set<SimpleGrantedAuthority> getAuthorities() {
        return role.getGrantedAuthorities();
    }
}
