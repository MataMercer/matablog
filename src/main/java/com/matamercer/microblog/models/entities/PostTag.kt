package com.matamercer.microblog.models.entities

import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "post_tags")
@Getter
@Setter
@NoArgsConstructor
class PostTag(
    @ManyToMany(mappedBy = "postTags")
    var posts: MutableSet<Post> = HashSet(),

    @Column(nullable = false, unique = true)
    var name: String? = null
) : BaseModel()