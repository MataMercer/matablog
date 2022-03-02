package com.matamercer.microblog.models.entities

import lombok.NoArgsConstructor
import lombok.AllArgsConstructor
import com.matamercer.microblog.models.entities.BaseModel
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Post
import lombok.Getter
import lombok.Setter
import javax.persistence.*

@Entity
@Table(name = "likes", uniqueConstraints = [UniqueConstraint(columnNames = ["fk_liker", "fk_post"])])
class Like(
    @field:ManyToOne
    @field:JoinColumn(name = "fk_liker")
    var liker: Blog,

    @field:ManyToOne
    @field:JoinColumn(name = "fk_post")
    var post: Post
) : BaseModel() {

}