package com.matamercer.microblog.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {
    STUDENT(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(UserPermission.COURSE_READ, UserPermission.COURSE_WRITE, UserPermission.STUDENT_READ, UserPermission.STUDENT_WRITE)),
    ADMINTRAINEE(Sets.newHashSet(UserPermission.COURSE_READ, UserPermission.STUDENT_READ));;

    private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

    public Set<? extends GrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }

//    public Set<Authority> getGrantedAuthorities(){
//        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
//                .collect(Collectors.toSet());
//        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
//
//        Set<Authority> grantedAuthorities = permissions.stream().map(permission -> new Authority()).collect(Collectors.toSet());
//        return permissions;
//    }
}