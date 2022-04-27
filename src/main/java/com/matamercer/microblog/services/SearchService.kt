package com.matamercer.microblog.services

import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.web.api.v1.dto.mappers.toPostResponseDto
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto
import org.hibernate.search.mapper.orm.Search
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class SearchService(
    private val entityManager: EntityManager
) {
    fun searchPosts(pageRequest: PageRequest, query: String): Page<PostResponseDto> {
        val searchSession = Search.session(entityManager)
        val res = searchSession.search(Post::class.java).where{it.match().fields("content").matching(query).fuzzy(1)}.fetch(pageRequest.offset.toInt(), pageRequest.pageSize)
        val hits = res.hits() as List<Post>
        return PageImpl<Post>(hits, pageRequest, res.total().hitCount()).map { it.toPostResponseDto() }
    }

}