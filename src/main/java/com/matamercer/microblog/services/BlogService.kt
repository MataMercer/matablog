package com.matamercer.microblog.services

import com.matamercer.microblog.exceptions.NotFoundException
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.BlogRepository
import com.matamercer.microblog.web.api.v1.dto.mappers.toBlogResponseDto
import com.matamercer.microblog.web.api.v1.dto.responses.BlogResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class BlogService @Autowired constructor(
    private val blogRepository: BlogRepository,
    private val userService: UserService,
) {
    @Transactional
    fun save(blog: Blog): Blog {
        return blogRepository.save(blog)
    }

    @Transactional
    fun createDefaultBlogForUser(user: User) {
        var blog = Blog(user.username, user.username, false)
        blog = save(blog)
        user.addBlog(blog)
        user.activeBlog = blog
        userService.save(user)
    }

    fun getBlog(id: Long): Blog {
        val blog = blogRepository.findById(id)
        return if (!blog.isPresent) {
            throw NotFoundException("Blog with id $id is not found.")
        } else {
            blog.get()
        }
    }

    fun getBlog(blogName: String): Blog {
        val blog = blogRepository.findByBlogName(blogName)
        return if (!blog!!.isPresent) {
            throw NotFoundException("Blog with name $blogName is not found.")
        } else {
            blog.get()
        }
    }

    fun getBlogResponseDto(id: Long): BlogResponseDto {
        val blog = getBlog(id)
        return blog.toBlogResponseDto()
    }
}