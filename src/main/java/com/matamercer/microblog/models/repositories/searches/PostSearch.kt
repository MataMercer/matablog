package com.matamercer.microblog.models.repositories.searches

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.enums.PostCategory
import com.matamercer.microblog.models.entities.PostTag
import org.springframework.data.jpa.repository.JpaRepository
import com.matamercer.microblog.models.entities.Follow
import lombok.Getter
import lombok.Setter

class PostSearch {
    var blog: Blog? = null
    var postCategory: PostCategory? = null
    var postTags: Set<PostTag>? = null
}