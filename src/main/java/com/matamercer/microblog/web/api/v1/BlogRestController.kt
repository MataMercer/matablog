package com.matamercer.microblog.web.api.v1

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.security.CurrentUser
import com.matamercer.microblog.security.UserPrincipal
import com.matamercer.microblog.services.BlogService
import com.matamercer.microblog.services.FollowService
import com.matamercer.microblog.web.api.v1.dto.requests.FollowRequestDto
import com.matamercer.microblog.web.api.v1.dto.responses.BlogResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/blog")
class BlogRestController @Autowired constructor(
    private val blogService: BlogService,
    private val followService: FollowService
) {
    @GetMapping("/name/{blogName}")
    fun getBlogByName(@PathVariable blogName: String): ResponseEntity<Blog> {
        return ResponseEntity.ok(blogService.getBlog(blogName))
    }

    @GetMapping("/{id}")
    fun getBlogById(@PathVariable id: Long): ResponseEntity<BlogResponseDto> {
        return ResponseEntity.ok(blogService.getBlogResponseDto(id))
    }

    @PostMapping("/{id}/follow")
    fun followBlog(
        @PathVariable id: String,
        followRequestDTO: @Valid FollowRequestDto,
        @CurrentUser userPrincipal: UserPrincipal
    ): ResponseEntity<*> {
        followService.followBlog(userPrincipal.activeBlog, id.toLong(), followRequestDTO)
        return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
    }

    @DeleteMapping("/{id}/follow")
    fun unfollowBlog(@PathVariable id: String, @CurrentUser userPrincipal: UserPrincipal): ResponseEntity<*> {
        followService.unfollowBlog(userPrincipal.activeBlog, id.toLong())
        return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/search")
    fun searchBlogs(
        @RequestParam(name = "query") query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int,
    ): ResponseEntity<*> {
        val blogs = blogService.searchBlogs(PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"), query)
        return ResponseEntity.ok(blogs)
    }
}