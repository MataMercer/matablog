package com.matamercer.microblog.security.authorization

import com.matamercer.microblog.models.entities.*
import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class UserAuthority(private val domainObjectClass: Class<out BaseModel?>, private val permission: String) {
    USER_READ(User::class.java, "read"), USER_WRITE(User::class.java, "write"), USER_MANAGE(
        User::class.java, "manage"
    ),
    BLOG_READ(Blog::class.java, "read"), BLOG_CREATE(Blog::class.java, "create"), BLOG_UPDATE(
        Blog::class.java,
        "update"
    ),
    BLOG_MANAGE(
        Blog::class.java, "manage"
    ),
    POST_READ(Post::class.java, "read"), POST_CREATE_NEW(
        Post::class.java,
        "create_new"
    ),
    POST_CREATE_COMMENT(Post::class.java, "create_reply"), POST_UPDATE(
        Post::class.java, "update"
    ),
    POST_MANAGE(Post::class.java, "manage"),
    FILE_READ(File::class.java, "read"), FILE_CREATE(File::class.java, "create"), FILE_UPDATE(
        File::class.java, "update"
    ),
    FILE_MANAGE(File::class.java, "manage");

    override fun toString(): String {
        return String.format("%s:%s", domainObjectClass.simpleName, permission)
    }

    fun toSimpleGrantedAuthority(): SimpleGrantedAuthority {
        return SimpleGrantedAuthority(toString())
    }
}