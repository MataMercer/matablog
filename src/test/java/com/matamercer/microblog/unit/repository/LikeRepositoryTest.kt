package com.matamercer.microblog.unit.repository

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Like
import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.models.repositories.LikeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class LikeRepositoryTest {
    @Autowired
    private val likeRepository: LikeRepository? = null

    @Autowired
    private val entityManager: TestEntityManager? = null

    @Test
    fun whenLikePost_returnPostLikeCount() {
        val likes: MutableSet<Like> = HashSet()
        var blog = Blog("blogName", "preferredBlogName", false)
        blog = entityManager!!.persist(blog)
        var post = Post(blog, "title", "content", false, false, true)
        post = entityManager.persist(post)
        var like = Like(blog, post)
        like = entityManager.persist(like)
        likes.add(like)
        val count = post?.let { likeRepository!!.countLikesByPost(it) }!!
        assertThat(count).isEqualTo(likes.size.toLong())
    }
}