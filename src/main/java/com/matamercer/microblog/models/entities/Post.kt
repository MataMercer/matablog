package com.matamercer.microblog.models.entities

import lombok.NoArgsConstructor
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.BaseModel
import com.matamercer.microblog.models.entities.PostTag
import java.util.HashSet
import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.models.entities.Like
import jdk.jfr.Enabled
import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.Type
import java.util.ArrayList
import javax.persistence.*

@Entity
@Table(name = "posts")
class Post(
    @field:JoinColumn(name = "blog_id", nullable = false)
    @field:ManyToOne
    var blog: Blog? = null,

    @field:Column(
        nullable = true
    )
    var title: String? = null,

    @field:Column(
        columnDefinition = "text"
    ) var content: String? = null,

    @field:Column(nullable = false) @field:Type(type = "boolean")
    var isCommunityTaggingEnabled: Boolean = false,

    @field:Column(nullable = false) @field:Type(type = "boolean")
    var isSensitive: Boolean = false,

    @field:Column(
        nullable = false
    ) @field:Type(
        type = "boolean"
    )
    var published: Boolean = false,

    @field:ManyToMany
    @field:JoinTable(
        name = "post_posttag",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "posttag_id")]
    )
    var postTags: MutableSet<PostTag> = HashSet(),

    @field:OneToMany(cascade = [CascadeType.ALL])
    @field:JoinColumn(name = "file_id")
    var attachments: MutableList<File> = ArrayList(),

    @field:ManyToOne
    @field:JoinColumn(name = "post_id")
    var parentPost: Post? = null,

    @field:OneToMany(mappedBy = "parentPost")
    var replies: MutableList<Post> = ArrayList(),

    @field:OneToMany(mappedBy = "post", cascade = [CascadeType.ALL])
    var likes: Set<Like> = HashSet()
) : BaseModel() {

    fun addPostTag(pt: PostTag) {
        postTags.add(pt)
        pt.posts.add(this)
    }

    fun removePostTag(pt: PostTag) {
        postTags.remove(pt)
        pt.posts.remove(this)
    }

    fun addReply(post: Post) {
        replies.add(post)
        post.parentPost = this;
    }

    fun removeReply(post: Post) {
        replies.remove(post)
        post.replies.remove(this)
    }
}