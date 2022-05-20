package com.matamercer.microblog.services

import com.matamercer.microblog.exceptions.NotFoundException
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.BlogRepository
import com.matamercer.microblog.web.api.v1.dto.mappers.toBlogResponseDto
import com.matamercer.microblog.web.api.v1.dto.responses.BlogResponseDto
import org.hibernate.search.mapper.orm.Search
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class BlogService @Autowired constructor(
    private val blogRepository: BlogRepository,
    private val userService: UserService,
    private val entityManager: EntityManager
) {
    @Transactional
    fun save(blog: Blog): Blog {
        return blogRepository.save(blog)
    }

    @Transactional
    fun createDefaultBlogForUser(user: User) {
        var blog = Blog(user.username!!, user.username!!, false)
        blog = save(blog)
        user.addBlog(blog)
        user.activeBlog = blog
        userService.save(user)
    }

    fun getBlog(id: Long): Blog? {
        return blogRepository.findById(id).orElseThrow {
            throw NotFoundException("Blog with id $id is not found.")
        }
    }

    fun getBlog(blogName: String): Blog? {
        return blogRepository.findByBlogName(blogName)?:throw
            NotFoundException("Blog with name $blogName is not found.")

    }

    fun getBlogResponseDto(id: Long): BlogResponseDto {
        val blog = getBlog(id)
        return blog!!.toBlogResponseDto()
    }

    fun searchBlogs(pageRequest: PageRequest, query:String): Page<BlogResponseDto>{
        val searchSession = Search.session(entityManager)
        val res = searchSession.search(Blog::class.java)
            .where{it.match()
                .fields("blogName",
                    "preferredBlogName")
                .matching(query)
                .fuzzy(1)}
            .fetch(pageRequest.offset.toInt(), pageRequest.pageSize)
        val hits = res.hits() as List<Blog>
        return PageImpl<Blog>(hits, pageRequest, res.total().hitCount()).map { it.toBlogResponseDto() }
    }
}