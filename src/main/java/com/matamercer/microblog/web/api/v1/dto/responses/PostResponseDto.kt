package com.matamercer.microblog.web.api.v1.dto.responses;

import com.matamercer.microblog.models.entities.Blog;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostResponseDto extends BaseModelResponseDto {

    private BlogResponseDto blog;

    private String content;

    private String title;

    private List<PostTagResponseDto> postTags;

    private List<FileResponseDto> attachments;

    private List<LikeResponseDto> likes;

    private List<PostResponseDto> replies;

    private boolean communityTaggingEnabled;

    private boolean sensitive;

    private boolean published;

    private String parentPostId;

}
