package com.matamercer.microblog.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="likes", uniqueConstraints = {@UniqueConstraint(columnNames = {"fk_liker", "fk_post"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Like extends BaseModel{

    @ManyToOne
    @JoinColumn(name="fk_liker")
    private Blog liker;

    @ManyToOne
    @JoinColumn(name="fk_post")
    private Post post;
}
