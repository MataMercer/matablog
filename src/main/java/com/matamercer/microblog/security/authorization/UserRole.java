package com.matamercer.microblog.security.authorization;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {

    READER(Sets.newHashSet(
            UserAuthority.FILE_READ,
            UserAuthority.BLOG_READ,
            UserAuthority.POST_READ,
            UserAuthority.POST_UPDATE,
            UserAuthority.POST_CREATE_COMMENT)),
    BLOGGER(Sets.union(Sets.newHashSet(
            UserAuthority.BLOG_CREATE,
            UserAuthority.BLOG_UPDATE,
            UserAuthority.FILE_CREATE,
            UserAuthority.FILE_UPDATE,
            UserAuthority.POST_CREATE_NEW
    ), READER.getPermissions())),
    ADMIN(Sets.union(Sets.newHashSet(
            UserAuthority.USER_READ,
            UserAuthority.USER_WRITE,
            UserAuthority.USER_MANAGE
    ), BLOGGER.getPermissions()))
    ;

    @Getter
    private final Set<UserAuthority> permissions;

    UserRole(Set<UserAuthority> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.toString()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}