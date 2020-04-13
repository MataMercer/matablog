package com.example.microblog.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseModel implements UserDetails {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Authority> authorities = new HashSet<>();

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isAccountNonExpired;

    @Column(nullable = false)
    private boolean isAccountNonLocked;

    @Column(nullable = false)
    private boolean isCredentialsNonExpired;

    @Column(nullable = false)
    private boolean isEnabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Collection<Post> posts = new ArrayList<>();

    public User() {}

    public User(
            String username,
            String password,
            boolean isAccountNonExpired,
            boolean isAccountNonLocked,
            boolean isCredentialsNonExpired,
            boolean isEnabled) {

        //this is really dumb
//        this.authorities = authorities.stream().map(permission -> new Authority(permission.getAuthority())).collect(Collectors.toSet());;

        this.username = username;
        this.password = password;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }


}
