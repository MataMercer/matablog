package com.matamercer.microblog.models.entities

import javax.persistence.*

@Entity
@Table(name = "follows")
class Follow(follower: Blog?, followee: Blog?, notificationsEnabled: Boolean, muted: Boolean) : BaseModel() {
    @ManyToOne
    @JoinColumn(name = "fk_follower_blog")
    private val follower: Blog? = null

    @ManyToOne
    @JoinColumn(name = "fk_followee_blog")
    private val followee: Blog? = null

    @Column
    private val notificationsEnabled = false

    @Column
    private val muted = false
}