package com.matamercer.microblog.models.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="file_id")
    private List<File> attachments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post parentPost;

    @OneToMany(mappedBy = "parentPost")
    private List<Post> replies = new ArrayList<>();



    @Column(nullable = true)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Type(type = "boolean")
    @Column(nullable = false)
    private boolean isCommunityTaggingEnabled;

    @Type(type = "boolean")
    @Column(nullable = false)
    private boolean isSensitive;

    @Type(type = "boolean")
    @Column(nullable = false)
    private boolean published;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();


    public Post(Blog blog, String title, String content, boolean isCommunityTaggingEnabled,
            boolean isSensitive, boolean published) {
        this.blog = blog;
        this.title = title;
        this.content = content;
        this.isCommunityTaggingEnabled = isCommunityTaggingEnabled;
        this.isSensitive = isSensitive;
        this.published = published;
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
