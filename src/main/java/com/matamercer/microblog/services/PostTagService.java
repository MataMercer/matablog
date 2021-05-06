package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.repositories.PostTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostTagService {

    @Autowired
    private PostTagRepository postTagRepository;

    public PostTag findOrCreateByName(String name) {
        PostTag postTag = postTagRepository.findByName(name);
        if (postTag == null) {
            postTag = postTagRepository.save(new PostTag(name));
        }
        return postTag;
    }

    public PostTag getTag(String name) {
        return postTagRepository.findByName(name);
    }

    public Set<PostTag> getTags(List<String> postTagNames){
        return postTagNames.stream().map(this::getTag).collect(Collectors.toSet());
    }

    public Map<PostTag, Integer> getTopTagsByPosts(Blog blog, int page, int pageSize){
        return postTagRepository.findByBlogSortedByMostUsedMap(blog, PageRequest.of(page, pageSize, Sort.Direction.ASC, "postTagCount"));
    }

    public void deleteTag(PostTag tag) {
        postTagRepository.delete(tag);
    }
}
