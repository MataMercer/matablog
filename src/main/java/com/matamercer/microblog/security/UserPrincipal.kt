package com.matamercer.microblog.security

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.security.authorization.UserAuthority
import com.matamercer.microblog.security.authorization.UserRole
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(
    var id: Long,
    private var username: String = "",
    private var password: String? = null,
    private var isAccountNonExpired: Boolean = true,
    var accountNonLocked: Boolean = true,
    var credentialsNonExpired: Boolean = true,
    var enabled: Boolean = true,
    var activeBlog: Blog,
    var role: UserRole,

    ): UserDetails {

    override fun getUsername() = username
    fun setUsername(username: String) {
        this.username = username
    }

    override fun getPassword() = password

    override fun isAccountNonExpired() = isAccountNonExpired

    override fun isAccountNonLocked() = accountNonLocked

    override fun isCredentialsNonExpired() = credentialsNonExpired

    override fun isEnabled() = enabled

    override fun getAuthorities(): MutableSet<SimpleGrantedAuthority> {
        return role.grantedAuthorities.toMutableSet()
    }

    fun hasAuthority(userAuthority: UserAuthority?): Boolean {
        return role.permissions.contains(userAuthority)
    }

}