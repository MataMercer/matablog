package com.matamercer.microblog.models.entities

import lombok.NoArgsConstructor
import com.matamercer.microblog.models.entities.BaseModel
import java.util.HashSet
import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.models.entities.Follow
import com.matamercer.microblog.models.entities.Like
import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.Type
import java.util.ArrayList
import javax.persistence.*

@Entity
@Table(name = "blogs")
class Blog(
    @field:Column(nullable = false) var blogName: String? = null,
    @field:Column(
        nullable = false
    ) var preferredBlogName: String? = null,

    @field:Column(nullable = false) val isSensitive: Boolean = false,

    @field:ManyToMany(mappedBy = "blogs")
    var users: MutableSet<User> = HashSet(),

    @field:OneToMany(cascade = [CascadeType.ALL], mappedBy = "blog")
    var posts: MutableList<Post> = ArrayList(),

    @field:OneToMany(cascade = [CascadeType.ALL], mappedBy = "owner")
    var files: MutableList<File> = ArrayList(),

    @field:OneToMany(mappedBy = "followee")
    var followers: MutableSet<Follow> = HashSet(),

    @field:OneToMany(mappedBy = "follower")
    var following: MutableSet<Follow> = HashSet(),

    @field:OneToMany(mappedBy = "liker")
    var likes: MutableSet<Like> = HashSet(),
    ) : BaseModel() {
}