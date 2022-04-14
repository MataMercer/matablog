package com.matamercer.microblog.models.repositories.specifications

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.models.entities.PostTag
import com.matamercer.microblog.models.enums.PostCategory
import com.matamercer.microblog.models.repositories.searches.PostSearch
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class PostSpecification(private val postSearch: PostSearch) : Specification<Post?> {
    override fun toPredicate(root: Root<Post?>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder): Predicate {
        val blog = root.get<Blog>("blog")
        val postTags = root.get<Set<PostTag>>("postTags")
        val predicates: MutableList<Predicate> = ArrayList()
        if (postSearch.blog != null) {
            predicates.add(criteriaBuilder.equal(blog, postSearch.blog))
        }
        if (postSearch.postTags != null && postSearch.postTags!!.isNotEmpty()) {
            for (pt in postSearch.postTags!!) {
                val join = root.join<Post, PostTag>("postTags")
                predicates.add(criteriaBuilder.equal(join.get<Any>("id"), pt.id))
            }
        }
        if (postSearch.postCategory != null) {
            when (postSearch.postCategory) {
                PostCategory.ROOT -> predicates.add(criteriaBuilder.isNull(root.get<Any>("parentPost")))
                PostCategory.MEDIA -> predicates.add(criteriaBuilder.isNotEmpty(root.get("attachments")))
                else -> predicates.add(criteriaBuilder.isNotNull(root.get<Any>("parentPost")))
            }
        }
        predicates.add(criteriaBuilder.isTrue(root.get("published")))
        return criteriaBuilder.and(*predicates.toTypedArray())
    }
}