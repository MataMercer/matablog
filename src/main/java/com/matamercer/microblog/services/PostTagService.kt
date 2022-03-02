package com.matamercer.microblog.services

import com.matamercer.microblog.Exceptions.NotFoundException
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.PostTag
import com.matamercer.microblog.models.repositories.PostTagRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class PostTagService @Autowired constructor(private val postTagRepository: PostTagRepository) {
    fun findOrCreateByName(name: String): PostTag {
        return postTagRepository.findByName(name) ?: postTagRepository.save(PostTag(name))
    }

    fun getTag(name: String): PostTag {
        return postTagRepository.findByName(name) ?: throw NotFoundException("Post tag not found")
    }

    fun getTags(postTagNames: List<String>): Set<PostTag> {
        return postTagNames.stream().map { name: String -> getTag(name) }.collect(Collectors.toSet())
    }

    fun getTopTagsByPosts(blog: Blog, page: Int, pageSize: Int): Map<PostTag, Int>? {
        return findByBlogSortedByMostUsedMap(
            blog,
            PageRequest.of(page, pageSize, Sort.Direction.ASC, "postTagCount")
        )
    }

    fun findByBlogSortedByMostUsedMap(blog: Blog, pageRequest: Pageable?): Map<PostTag, Int>? {
        return postTagRepository.findByBlogSortedByMostUsed(blog, pageRequest).toList().associate { it.postTag to it.postTagCount }
            .toList().sortedBy { it.second }.toMap()
    }

    fun deleteTag(tag: PostTag) {
        postTagRepository.delete(tag)
    }
}