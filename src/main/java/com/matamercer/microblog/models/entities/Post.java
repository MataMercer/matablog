package com.matamercer.microblog.models.entities;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Posts")
@Getter
@Setter
public class Post extends BaseModel {
    @ManyToOne
    private User user;

    @Type(type = "text")
    private String content;
}
