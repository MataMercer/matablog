package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    PostTag findByName(String name);


    //gets top 4 most used tags on the blog
    //SELECT name FROM "post_tags" WHERE id IN (SELECT posttag_id FROM "post_posttag" WHERE post_id IN (SELECT id FROM "posts" WHERE "posts".blog_id=1) GROUP BY posttag_id ORDER BY count(posttag_id) DESC LIMIT 4);
    //join version
    //SELECT name FROM "post_tags" INNER JOIN (SELECT posttag_id FROM (SELECT * FROM "posts" WHERE blog_id=1) AS blogposts INNER JOIN "post_posttag" ON blogposts.id="post_posttag".post_id GROUP BY posttag_id ORDER BY count(posttag_id) DESC LIMIT 4) AS topPostTagIds ON "post_tags".id=topPostTagIds.posttag_id;


    //reference jpql @Query("SELECT l FROM LoginToken l INNER JOIN l.user u WHERE u.username = :username")
    @Query("SELECT pt as postTag, COUNT(pt.id) as postTagCount FROM PostTag pt INNER JOIN pt.posts p WHERE p.blog = :blog GROUP BY pt.id")
//    @Query(
//            value = "SELECT id, created_at, updated_at, name FROM post_tags INNER JOIN (SELECT posttag_id FROM (SELECT * FROM posts WHERE blog_id=?1) AS blogposts INNER JOIN post_posttag ON blogposts.id=post_posttag.post_id GROUP BY posttag_id ORDER BY count(posttag_id) DESC LIMIT ?2) AS topPostTagIds ON post_tags.id=topPostTagIds.posttag_id",
//            nativeQuery = true
//    )
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
