package com.matamercer.microblog.models.repositories.searches;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.enums.PostCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PostSearch {
    private Blog blog;
    private PostCategory postCategory;
    private Set<PostTag> postTags;
}
