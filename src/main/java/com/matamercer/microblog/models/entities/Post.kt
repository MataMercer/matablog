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
import java.util.ArrayList
import javax.persistence.*

@Entity
@Table(name = "posts")
class Post(
    @JoinColumn(name = "blog_id", nullable = false)
    @ManyToOne
    var blog: Blog? = null,

    @Column(
        nullable = true
    )
    var title: String? = null,

    @Column(
        columnDefinition = "text"
    ) var content: String? = null,

    @Column(nullable = false)
    var isCommunityTaggingEnabled: Boolean = false,

    @Column(nullable = false)
    var isSensitive: Boolean = false,

    @Column(
        nullable = false
    )
    var published: Boolean = false,

    @ManyToMany
    @JoinTable(
        name = "post_posttag",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "posttag_id")]
    )
    var postTags: MutableSet<PostTag> = HashSet(),

    @OneToMany(cascade = [
        CascadeType.PERSIST,
        CascadeType.MERGE
    ])
    @JoinColumn(name = "file_id")
    var attachments: MutableList<File> = ArrayList(),

    @ManyToOne
    @JoinColumn(name = "post_id")
    var parentPost: Post? = null,

    @OneToMany(mappedBy = "parentPost")
    var replies: MutableList<Post> = ArrayList(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL])
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
        post.parentPost = this
    }

    fun removeReply(post: Post) {
        replies.remove(post)
        post.replies.remove(this)
    }
}