package com.matamercer.microblog.models.enums;

import com.matamercer.microblog.models.entities.File;
import com.matamercer.microblog.models.entities.Post;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public enum PostCategory {
    ROOT("root"),
    MEDIA("media"),
    REPLY("reply");

    public final String postCategory;

    PostCategory(String category){
        this.postCategory = category;
    }

    public static Set<PostCategory> getPostCategories(Post post){
        Set<PostCategory> postCategories = new HashSet<>();
        if(!Optional.ofNullable(post.getParentPost()).isPresent()){
            postCategories.add(ROOT);
        }else{
            postCategories.add(REPLY);
        }

        if(post.getAttachments().size() > 0){
            postCategories.add(MEDIA);
        }
        return postCategories;
    }

    public static Set<PostCategory> getPostCategories(Post parentPost, List<File> attachments){
        Set<PostCategory> postCategories = new HashSet<>();
        if(!Optional.ofNullable(parentPost).isPresent()){
            postCategories.add(ROOT);
        }else{
            postCategories.add(REPLY);
        }

        if(attachments.size() > 0){
            postCategories.add(MEDIA);
        }
        return postCategories;
    }



}
