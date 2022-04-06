package com.matamercer.microblog.models.entities

import javax.persistence.*

@Entity
@Table(name = "follows")
class Follow(
    @ManyToOne
    @JoinColumn(name = "fk_follower_blog")
    private val follower: Blog? = null,

    @ManyToOne
    @JoinColumn(name = "fk_followee_blog")
    private val followee: Blog? = null,

    @Column
    private val notificationsEnabled: Boolean = false,

    @Column
    private val muted: Boolean = false
): BaseModel(){}