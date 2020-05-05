package com.matamercer.microblog.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreatePostForm {
    @NotEmpty
    private String content;
}
