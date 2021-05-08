package com.matamercer.microblog.forms;

import com.matamercer.microblog.models.entities.PostTag;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
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
