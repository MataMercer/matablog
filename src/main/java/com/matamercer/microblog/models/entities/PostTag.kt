package com.matamercer.microblog.models.entities

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "post_tags")
class PostTag(
    @ManyToMany(mappedBy = "postTags")
    var posts: MutableSet<Post> = HashSet(),

    @Column(nullable = false, unique = true)
    @KeywordField
    var name: String = ""
) : BaseModel()