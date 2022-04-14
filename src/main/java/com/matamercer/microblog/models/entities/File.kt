package com.matamercer.microblog.models.entities

import javax.persistence.*

@Entity
@Table(name = "files")
class File(
    @Column(nullable = false)
    var name: String,

    @ManyToOne
    @JoinColumn(name = "blog_id")
    var owner: Blog? = null,
    ) : BaseModel() {

}