package com.matamercer.microblog.forms;

import com.matamercer.microblog.models.entities.PostTag;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

@Data
public class CreatePostForm {
    @NotEmpty
    private String content;
    
    private String title;

    private List<String> postTags;

    private boolean communityTaggingEnabled;

    private boolean sensitive;
}
