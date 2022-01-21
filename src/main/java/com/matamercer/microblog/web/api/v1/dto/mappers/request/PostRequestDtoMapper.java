package com.matamercer.microblog.web.api.v1.dto.mappers.request;

import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PostRequestDtoMapper {

   @Mapping(target = "attachments", ignore = true)
   @Mapping(target = "postTags", ignore = true)
   public abstract Post map(PostRequestDto postRequestDto);

//   protected PostTag mapPostTag(String name){
//      return new PostTag(name);
//   }

}
