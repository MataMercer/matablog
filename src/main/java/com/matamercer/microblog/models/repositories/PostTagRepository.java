package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    PostTag findByName(String name);

    @Query("SELECT pt as postTag, COUNT(pt.id) as postTagCount FROM PostTag pt INNER JOIN pt.posts p WHERE p.blog = :blog GROUP BY pt.id")
    Page<IPostTagCount> findByBlogSortedByMostUsed(Blog blog, Pageable pageRequest);

    default Map<PostTag, Integer> findByBlogSortedByMostUsedMap(Blog blog, Pageable pageRequest){
        Map<PostTag, Integer> unorderedMap =  findByBlogSortedByMostUsed(blog, pageRequest)
                .toList()
                .stream()
                .collect(
                        Collectors
                                .toMap(IPostTagCount::getPostTag, IPostTagCount::getPostTagCount));

        return unorderedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    interface IPostTagCount{
        PostTag getPostTag();
        Integer getPostTagCount();
    }

}
