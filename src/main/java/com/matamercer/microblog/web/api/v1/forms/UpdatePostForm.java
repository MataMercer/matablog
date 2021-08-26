package com.matamercer.microblog.web.api.v1.forms;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdatePostForm extends CreatePostForm {
    @NotNull
    private String postId;

    private List<String> filesToDelete;
}
