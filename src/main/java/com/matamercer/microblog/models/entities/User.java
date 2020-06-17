package com.matamercer.microblog.models.entities;

import com.matamercer.microblog.models.entities.Authority;
import com.matamercer.microblog.models.entities.BaseModel;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseModel implements UserDetails {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Authority> authorities = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_blog",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "blog_id") })
    private Set<Blog> blogs = new HashSet<Blog>();

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

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

//    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
//    private Collection<Post> posts = new ArrayList<>();

    public User() {}

    public User(
            String username,
            String password,
            boolean isAccountNonExpired,
            boolean isAccountNonLocked,
            boolean isCredentialsNonExpired,
            boolean isEnabled) {
        this.username = username;
        this.password = password;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }


}
