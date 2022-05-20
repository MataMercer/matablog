package com.matamercer.microblog.models.repositories.searches

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.enums.PostCategory
import com.matamercer.microblog.models.entities.PostTag

class PostSearch {
    var blogs: Set<Blog>? = null
    var postCategory: PostCategory? = null
    var postTags: Set<PostTag>? = null
}