package com.matamercer.microblog.models.enums

import com.matamercer.microblog.models.entities.File
import com.matamercer.microblog.models.entities.Post
import java.util.*

enum class PostCategory(val postCategory: String) {
    ROOT("root"),
    MEDIA("media"),
    REPLY("reply");

    companion object {
        fun getPostCategories(post: Post): Set<PostCategory> {
            val postCategories: MutableSet<PostCategory> = HashSet()
            if (!Optional.ofNullable(post.parentPost).isPresent) {
                postCategories.add(ROOT)
            } else {
                postCategories.add(REPLY)
            }
            if (post.attachments.size > 0) {
                postCategories.add(MEDIA)
            }
            return postCategories
        }

        fun getPostCategories(parentPost: Post?, attachments: List<File?>): Set<PostCategory> {
            val postCategories: MutableSet<PostCategory> = HashSet()
            if (!Optional.ofNullable(parentPost).isPresent) {
                postCategories.add(ROOT)
            } else {
                postCategories.add(REPLY)
            }
            if (attachments.isNotEmpty()) {
                postCategories.add(MEDIA)
            }
            return postCategories
        }
    }
}