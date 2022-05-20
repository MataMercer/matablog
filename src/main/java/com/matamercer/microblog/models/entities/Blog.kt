package com.matamercer.microblog.models.entities

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import javax.persistence.*

@Entity
@Table(name = "blogs")
@Indexed
class Blog(
    @Column(nullable = false)
    @FullTextField
    var blogName: String = "My Awesome Blog",
    @Column(nullable = false)
    @FullTextField
    var preferredBlogName: String = "My Even Awesome Blog",

    @Column(nullable = false)
    val isSensitive: Boolean = false,

    @ManyToMany(mappedBy = "blogs")
    var users: MutableSet<User> = HashSet(),

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "blog")
    var posts: MutableList<Post> = ArrayList(),

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "owner")
    var files: MutableList<File> = ArrayList(),

    @OneToMany(mappedBy = "followee")
    var followers: MutableSet<Follow> = HashSet(),

    @OneToMany(mappedBy = "follower")
    var following: MutableSet<Follow> = HashSet(),

    @OneToMany(mappedBy = "liker")
    var likes: MutableSet<Like> = HashSet(),
) : BaseModel() {
}