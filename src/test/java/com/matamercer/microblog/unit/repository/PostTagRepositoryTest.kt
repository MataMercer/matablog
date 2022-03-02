package com.matamercer.microblog.unit.repository

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.models.entities.PostTag
import com.matamercer.microblog.models.repositories.PostTagRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class PostTagRepositoryTest {
    @Autowired
    private val entityManager: TestEntityManager? = null

    @Autowired
    private val postTagRepository: PostTagRepository? = null
    @Test
    @Throws(Exception::class)
    fun whenFindByBlogSortedByMostUsed_thenReturnListOfPostTags() {
//        val testTagName1 = "tag1"
//        var pt1 = PostTag(testTagName1)
//        pt1 = entityManager!!.persist(PostTag(testTagName1))
//        val testTagName2 = "tag2"
//        var pt2 = PostTag(testTagName2)
//        pt2 = entityManager.persist(PostTag(testTagName2))
//        var blog = Blog("BlogName", "PreferredBlogName", false)
//        blog = entityManager.persist(blog)
//        var post = Post(blog, "", "", false, false, true)
//        post = entityManager.persist(post)
//        post.addPostTag(pt1)
//        var post2 = Post(blog, "", "", false, false, true)
//        post2 = entityManager.persist(post2)
//        post2.addPostTag(pt1)
//        post2.addPostTag(pt2)
//        val page = 0
//        var pageSize = 10
//        val postTagPage = postTagRepository!!.findByBlogSortedByMostUsedMap(
//            blog,
//            PageRequest.of(page, pageSize, Sort.Direction.ASC, "postTagCount")
//        )
//        Assertions.assertThat(postTagPage!![pt1]).isEqualTo(2)
//        Assertions.assertThat(postTagPage[pt2]).isEqualTo(1)
//        pageSize = 1
//        val postTagPage2 = postTagRepository.findByBlogSortedByMostUsedMap(blog, PageRequest.of(page, pageSize))
//        Assertions.assertThat(postTagPage2!![pt2]).isEqualTo(null)
    }
}