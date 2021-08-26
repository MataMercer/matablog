package com.matamercer.microblog.models.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.matamercer.microblog.models.enums.PostCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToMany
    @JoinTable(name = "post_posttag",
        joinColumns = { @JoinColumn(name = "post_id") },
        inverseJoinColumns = { @JoinColumn(name = "posttag_id" ) }
    )
    private Set<PostTag> postTags = new HashSet<>();

    @OneToMany
    @JoinColumn(name="file_id")
    private List<File> attachments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post parentPost;

    @OneToMany(mappedBy = "parentPost")
    @JsonBackReference
    private List<Post> replies = new ArrayList<>();



    @Type(type = "text")
    private String title;

    @Type(type = "text")
    @Column(nullable = false)
    private String content;

    @Type(type = "boolean")
    @Column(nullable = false)
    private boolean isCommunityTaggingEnabled;

    @Type(type = "boolean")
    @Column(nullable = false)
    private boolean isSensitive;

    @Type(type = "boolean")
    @Column(nullable = false)
    private boolean isPublished;

    public Post(Blog blog, String title, String content, boolean isCommunityTaggingEnabled,
            boolean isSensitive, boolean isPublished) {
        this.blog = blog;
        this.title = title;
        this.content = content;
        this.isCommunityTaggingEnabled = isCommunityTaggingEnabled;
        this.isSensitive = isSensitive;
        this.isPublished = isPublished;
    }

    public void addPostTag(PostTag pt) {
        this.postTags.add(pt);
        pt.getPosts().add(this);
    }

    public void removePostTag(PostTag pt) {
        this.postTags.remove(pt);
        pt.getPosts().remove(this);
    }

    public void addReply(Post post) {
        this.replies.add(post);
        post.getReplies().add(this);
    }

    public void removeReply(Post post) {
        this.replies.remove(post);
        post.getReplies().remove(this);
    }
    
}
