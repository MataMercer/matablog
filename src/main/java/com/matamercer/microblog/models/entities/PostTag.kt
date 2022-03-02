package com.matamercer.microblog.models.entities

import lombok.NoArgsConstructor
import com.matamercer.microblog.models.entities.BaseModel
import javax.persistence.ManyToMany
import com.matamercer.microblog.models.entities.Post
import lombok.Getter
import lombok.Setter
import java.util.HashSet
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "post_tags")
@Getter
@Setter
@NoArgsConstructor
class PostTag : BaseModel {
    @ManyToMany(mappedBy = "postTags")
    var posts: MutableSet<Post> = HashSet()

    @Column(nullable = false, unique = true)
    var name: String

    constructor(posts: MutableSet<Post>, name: String) {
        this.posts = posts
        this.name = name
    }

    constructor(name: String) {
        this.name = name
    }
}