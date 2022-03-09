package com.matamercer.microblog.models.entities

import javax.persistence.*

@Entity
@Table(name = "files")
class File(
    @field:Column(nullable = false)
    var name: String? = null,

    @field:ManyToOne
    @field:JoinColumn(name = "blog_id")
    var owner: Blog? = null,
    ) : BaseModel() {

}