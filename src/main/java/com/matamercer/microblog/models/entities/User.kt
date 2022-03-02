package com.matamercer.microblog.models.entities

import org.springframework.security.core.userdetails.UserDetails
import java.util.HashSet
import org.springframework.security.core.authority.SimpleGrantedAuthority
import com.matamercer.microblog.security.authorization.UserAuthority
import com.matamercer.microblog.security.authorization.UserRole
import java.util.ArrayList
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    @field:Column(unique = true)
    var email: String? = null,

    @field:OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
    var userKeyPairs: List<UserKeyPair> = ArrayList(),

    @field:ManyToMany
    @field:JoinTable(
        name = "user_blog",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "blog_id")]
    )
    var blogs: MutableSet<Blog> = HashSet(),

    @field:Column(unique = true)
    private var username: String? = null,

    @field:Column
    private var password: String? = null,

    @field:Column(nullable = false)
    private var isAccountNonExpired: Boolean = true,

    @field:Column(nullable = false)
    var accountNonLocked: Boolean = true,

    @field:Column(nullable = false)
    var credentialsNonExpired: Boolean = true,

    @field:Column(nullable = false)
    var enabled: Boolean = true,

    @field:OneToOne
    @field:JoinColumn(name = "blog_id")
    var activeBlog: Blog? = null,

    @field:Enumerated(EnumType.STRING)
    @Column
    var authenticationProvider: AuthenticationProvider? = null,

    @Enumerated(EnumType.STRING)
    @Column
    var role: UserRole? = null,

    @Column
    var oAuth2Id: String? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var refreshToken: Set<RefreshToken> = HashSet()

) : BaseModel(), UserDetails {

    constructor(email: String?,
                username: String?,
                password: String?,
                role: UserRole,
                authenticationProvider: AuthenticationProvider?) : this() {
        this.email = email
        this.username = username
        this.password = password
        this.role = role
        this.authenticationProvider = authenticationProvider
    }

    override fun getUsername() = username
    fun setUsername(username: String) {
        this.username = username
    }

    override fun getPassword() = password

    override fun isAccountNonExpired() = isAccountNonExpired

    override fun isAccountNonLocked() = accountNonLocked

    override fun isCredentialsNonExpired() = credentialsNonExpired

    override fun isEnabled() = enabled
    fun addBlog(b: Blog) {
        blogs.add(b)
        b.users.add(this)
    }

    fun removeBlog(b: Blog) {
        blogs.remove(b)
        b.users.remove(this)
    }

    override fun getAuthorities(): MutableSet<SimpleGrantedAuthority> {
        return role!!.grantedAuthorities.toMutableSet()
    }

    fun hasAuthority(userAuthority: UserAuthority?): Boolean {
        return role!!.permissions.contains(userAuthority)
    }
}