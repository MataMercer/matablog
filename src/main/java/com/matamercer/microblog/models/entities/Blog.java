package com.matamercer.microblog.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "blogs")
@Getter
@Setter
public class Blog extends BaseModel{

    @ManyToMany(mappedBy="blogs")
    private Set<User> users = new HashSet<User>();

    @Column(nullable = false)
    private String blogname;

    @Column(nullable = false)
    private String preferredBlogName;
}
