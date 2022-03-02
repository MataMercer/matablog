package com.matamercer.microblog.security.authorization

import com.google.common.collect.Sets
import lombok.Getter
import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class UserRole( val permissions: Set<UserAuthority>) {
    READER(
        Sets.newHashSet(
            UserAuthority.FILE_READ,
            UserAuthority.BLOG_READ,
            UserAuthority.POST_READ,
            UserAuthority.POST_UPDATE,
            UserAuthority.POST_CREATE_COMMENT
        )
    ),
    BLOGGER(
        Sets.union(
            Sets.newHashSet(
                UserAuthority.BLOG_CREATE,
                UserAuthority.BLOG_UPDATE,
                UserAuthority.FILE_CREATE,
                UserAuthority.FILE_UPDATE,
                UserAuthority.POST_CREATE_NEW
            ), READER.permissions
        )
    ),
    ADMIN(
        Sets.union(
            Sets.newHashSet(
                UserAuthority.USER_READ,
                UserAuthority.USER_WRITE,
                UserAuthority.USER_MANAGE
            ), BLOGGER.permissions
        )
    );

    val grantedAuthorities: Set<SimpleGrantedAuthority>
        get() {
            return this.permissions.map { userAuthority -> userAuthority.toSimpleGrantedAuthority() }.toSet()
        }
}