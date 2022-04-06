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
    @ManyToOne
    @JoinColumn(name = "fk_liker")
    var liker: Blog? = null,

    @ManyToOne
    @JoinColumn(name = "fk_post")
    var post: Post? = null
) : BaseModel() {

}