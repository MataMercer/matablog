package com.matamercer.microblog.models.entities

import javax.persistence.*

@Entity
@Table(name = "follows")
class Follow(
    @ManyToOne
    @JoinColumn(name = "fk_follower_blog")
    val follower: Blog? = null,

    @ManyToOne
    @JoinColumn(name = "fk_followee_blog")
    val followee: Blog? = null,

    @Column
    val notificationsEnabled: Boolean = false,

    @Column
    val muted: Boolean = false
): BaseModel(){}