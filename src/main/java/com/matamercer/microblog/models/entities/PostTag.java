package com.matamercer.microblog.models.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "post_tags")
@Getter
@Setter
public class PostTag extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Type(type = "text")
    @Column(nullable = false)
    private String name;
}