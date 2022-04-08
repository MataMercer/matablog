package com.matamercer.microblog.models.entities

import javax.persistence.*

@Entity
@Table(name = "likes", uniqueConstraints = [UniqueConstraint(columnNames = ["fk_liker", "fk_post"])])
class Like(
    @ManyToOne
    @JoinColumn(name = "fk_liker")
    var liker: Blog? = null,

    @ManyToOne
    @JoinColumn(name = "fk_post")
    var post: Post? = null
) : BaseModel() {

}