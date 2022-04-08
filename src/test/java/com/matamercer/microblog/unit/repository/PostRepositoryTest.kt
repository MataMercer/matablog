package com.matamercer.microblog.unit.repository

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.models.entities.PostTag
import com.matamercer.microblog.models.enums.PostCategory
import com.matamercer.microblog.models.repositories.PostRepository
import com.matamercer.microblog.models.repositories.searches.PostSearch
import com.matamercer.microblog.models.repositories.specifications.PostSpecification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Collectors

@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {
    @Autowired
    private val entityManager: TestEntityManager? = null

    @Autowired
    private val postRepository: PostRepository? = null

    //    @Test
    //    public void whenFindByBlog_returnBlog(){
    //        Blog blog = new Blog("blogName", "preferredBlogName", false);
    //        entityManager.persist(blog);
    //
    //        Post post = new Post(blog, "title", "content", false, false);
    //        entityManager.persist(post);
    //
    //        int page = 0;
    //        int pageSize = 10;
    //        PageRequest pageRequest = PageRequest.of(page, pageSize);
    //
    //        Page<Post> postPage =  postRepository.findByBlog(blog, pageRequest);
    //        List<Post> postList= postPage.get().collect(Collectors.toList());
    //        Post foundPost = postList.get(0);
    //
    //        assertThat(foundPost).isEqualTo(post);
    //    }
    @Test
    fun whenSearchByBlog_ReturnFilteredPosts() {
        val blog = Blog("blogName", "preferredBlogName", false)
        entityManager!!.persist(blog)
        val post = Post(blog, "title", "content", false, false, true)
        entityManager.persist(post)
        val page = 0
        val pageSize = 10
        val pageRequest = PageRequest.of(page, pageSize)
        val postSearch = PostSearch()
        postSearch.blog = blog
        val postSpecification = PostSpecification(postSearch)
        val postPage = postRepository!!.findAll(postSpecification, pageRequest)
        val postList = postPage.get().collect(Collectors.toList())
        val foundPost = postList[0]
        assertThat(foundPost).isEqualTo(post)
    }

    @Test
    fun whenSearchByTag_ReturnFilteredPosts() {
        val blog = Blog("blogName", "preferredBlogName", false)
        entityManager!!.persist(blog)
        val post = Post(blog, "title", "content", false, false, true)
        entityManager.persist(post)
        val post2 = Post(blog, "title", "content", false, false, true)
        entityManager.persist(post2)
        val postTag = PostTag(name = "postTag1")
        entityManager.persist(postTag)
        val postTag2 = PostTag(name = "postTag2")
        entityManager.persist(postTag2)
        post.addPostTag(postTag)
        post2.addPostTag(postTag)
        post2.addPostTag(postTag2)
        entityManager.persist(post)
        entityManager.persist(post2)
        val page = 0
        val pageSize = 10
        val pageRequest = PageRequest.of(page, pageSize)
        val postSearch = PostSearch()
        postSearch.blog = blog
        postSearch.postTags = setOf(postTag, postTag2)
        postSearch.postCategory = PostCategory.ROOT
        val postSpecification = PostSpecification(postSearch)
        val postPage = postRepository!!.findAll(postSpecification, pageRequest)
        val postList = postPage.get().collect(Collectors.toList())
        val foundPost = postList[0]
        assertThat(foundPost).isEqualTo(post2)
        assertThat(postList.size).isEqualTo(1)
    }
}