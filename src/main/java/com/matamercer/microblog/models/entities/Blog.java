package com.matamercer.microblog.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "blogs")
@Getter
@Setter
@NoArgsConstructor
public class Blog extends BaseModel{

    @ManyToMany(mappedBy="blogs")
    private Set<User> users = new HashSet<User>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "blog")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private List<File> files = new ArrayList<>();

    @Column(nullable = false)
    private String blogName;

    @Column(nullable = false)
    private String preferredBlogName;

    @Type(type = "boolean")
    @Column(nullable = false)
    private boolean isSensitive;

    @OneToMany(mappedBy = "followee")
    private Set<Follow> followers = new HashSet<>();

    @OneToMany(mappedBy = "follower")
    private Set<Follow> following = new HashSet<>();

    @OneToMany(mappedBy = "liker")
    private Set<Like> likes = new HashSet<>();

    public Blog(String blogName, String preferredBlogName, boolean isSensitive){
        this.blogName = blogName;
        this.preferredBlogName = preferredBlogName;
        this.isSensitive = isSensitive;
    }
}
