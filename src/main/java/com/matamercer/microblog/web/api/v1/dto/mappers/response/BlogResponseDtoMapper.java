package com.matamercer.microblog.web.api.v1.dto.mappers.response;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.web.api.v1.dto.responses.BlogResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BlogResponseDtoMapper {
    public abstract BlogResponseDto map(Blog blog);
}
