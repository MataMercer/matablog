package com.matamercer.microblog.web.api.v1.forms;

import lombok.Data;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreatePostForm {
    @NotNull
    @Size(min=1, max=10000, message = "Length should be between 1 and 10000 characters.")
    private String content;

    @Size(min=0, max=100, message = "Length should be between 1 and 100 characters.")
    private String title;

    private List<String> postTags;

    private boolean communityTaggingEnabled;

    private boolean sensitive;

    private String parentPostId;

}
