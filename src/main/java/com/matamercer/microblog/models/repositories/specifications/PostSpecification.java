package com.matamercer.microblog.models.repositories.specifications;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.File;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.enums.PostCategory;
import com.matamercer.microblog.models.repositories.searches.PostSearch;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PostSpecification implements Specification<Post> {

    private final PostSearch postSearch;

    public PostSpecification(PostSearch postSearch){
        this.postSearch = postSearch;
    }

    @Override
    public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<Blog> blog = root.get("blog");
        Path<Set<PostTag>> postTags = root.get("postTags");
        final List<Predicate> predicates = new ArrayList<>();
        if(postSearch.getBlog() != null){
            predicates.add(criteriaBuilder.equal(blog, postSearch.getBlog()));
        }
        if(postSearch.getPostTags() != null && !postSearch.getPostTags().isEmpty()){
            for(PostTag pt: postSearch.getPostTags()){
                Join<Post, PostTag> join = root.join("postTags");
                predicates.add(criteriaBuilder.equal(join.get("id"), pt.getId()));
            }
        }
        if(postSearch.getPostCategory() != null){
            switch(postSearch.getPostCategory()){
                case ROOT:
                    predicates.add(criteriaBuilder.isNull(root.get("parentPost")));
                    break;
                case MEDIA:
                    predicates.add(criteriaBuilder.isNotEmpty(root.get("attachments")));
                    break;
                case REPLY:
                    predicates.add(criteriaBuilder.isNotNull(root.get("parentPost")));
                    break;
            }
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
