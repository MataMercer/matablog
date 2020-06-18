package com.matamercer.microblog.models.entities;

import lombok.Getter;
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
public class Blog extends BaseModel{

    @ManyToMany(mappedBy="blogs")
    private Set<User> users = new HashSet<User>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "blog")
    private List<Post> posts = new ArrayList<>();

    @Column(nullable = false)
    private String blogname;

    @Column(nullable = false)
    private String preferredBlogName;

    @Type(type = "boolean")
    @Column(nullable = false)
    private boolean isSensitive;
}
