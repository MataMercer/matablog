package com.matamercer.microblog.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="follows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends BaseModel{

    @ManyToOne
    @JoinColumn(name = "fk_follower_blog")
    private Blog follower;

    @ManyToOne
    @JoinColumn(name= "fk_followee_blog")
    private Blog followee;

    @Column
    private boolean notificationsEnabled;

    @Column
    private boolean muted;
}
