package com.matamercer.microblog.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post_tags")
@Getter
@Setter
@NoArgsConstructor
public class PostTag extends BaseModel {
    @ManyToMany(mappedBy="postTags")
    private Set<Post> posts = new HashSet<Post>();

    @Column(nullable = false, unique = true)
    private String name;

    public PostTag(Set<Post> posts, String name) {
        this.posts = posts;
        this.name = name;
    }

    public PostTag(String name) {
        this.name = name;
    }

}