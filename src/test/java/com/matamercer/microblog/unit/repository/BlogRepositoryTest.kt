package com.matamercer.microblog.unit.repository

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.repositories.BlogRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class BlogRepositoryTest {
    @Autowired
    private val blogRepository: BlogRepository? = null

    @Autowired
    private val entityManager: TestEntityManager? = null
    @Test
    fun whenFindByBlogName_returnBlog() {
        var blog = Blog("BlogName", "PreferredBlogName", false)
        blog = entityManager!!.persist(blog)
        val optionalFoundBlog = blogRepository!!.findByBlogName(blog.blogName)
        var foundBlog: Blog? = null
        if (optionalFoundBlog!!.isPresent) {
            foundBlog = optionalFoundBlog.get()
        }
        assertThat(foundBlog).isEqualTo(blog)
    }
}