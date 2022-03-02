package com.matamercer.microblog.models.entities

import javax.persistence.*

@Entity
@Table(name = "files")
class File : BaseModel() {
    @Column(nullable = false)
    var name: String? = null

    @ManyToOne
    @JoinColumn(name = "blog_id")
    var owner: Blog? = null
}