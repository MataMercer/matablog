package com.matamercer.microblog.security.authorization;

import com.matamercer.microblog.models.entities.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserAuthority {
    //User
    USER_READ(User.class, "read"),
    USER_WRITE(User.class, "write"),
    USER_MANAGE(User.class, "manage"),

    //Blog
    BLOG_READ(Blog.class, "read"),
    BLOG_CREATE(Blog.class, "create"),
    BLOG_UPDATE(Blog.class, "update"),
    BLOG_MANAGE(Blog.class, "manage"),

    //Post
    POST_READ(Post.class, "read"),
    POST_CREATE_NEW(Post.class, "create_new"),
    POST_CREATE_COMMENT(Post.class, "create_reply"),
    POST_UPDATE(Post.class, "update"),
    POST_MANAGE(Post.class, "manage"),


    //File
    FILE_READ(File.class, "read"),
    FILE_CREATE(File.class, "create"),
    FILE_UPDATE(File.class, "update"),
    FILE_MANAGE(File.class, "manage");

    private final String permission;
    private final Class<? extends BaseModel> domainObjectClass;

    UserAuthority(Class<? extends BaseModel> domainObjectClass, String permission) {
        this.domainObjectClass = domainObjectClass;
        this.permission = permission;
    }

    public String toString(){
        return String.format("%s:%s", domainObjectClass.getSimpleName(), permission);
    }
    public SimpleGrantedAuthority toSimpleGrantedAuthority(){
        return new SimpleGrantedAuthority(toString());
    }
}