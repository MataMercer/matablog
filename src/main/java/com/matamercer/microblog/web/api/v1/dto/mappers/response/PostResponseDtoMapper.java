package com.matamercer.microblog.web.api.v1.dto.mappers.response;

import com.matamercer.microblog.models.entities.File;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.web.api.v1.FileRestController;
import com.matamercer.microblog.web.api.v1.dto.mappers.DateMapper;
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = DateMapper.class)
public abstract class PostResponseDtoMapper {

//    @Mapping(target = "attachmentUrls", source = "source.attachments")
    public abstract PostResponseDto map(Post source);
//    public String zonedDateTimeToString(ZonedDateTime zonedDateTime){
//        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
//    }
//    List<String> mapAttachments(List<File> source){
//        return source.stream().map(elem ->
//                linkTo(methodOn(FileRestController.class)
//                        .serveFile(elem.getId(), elem.getName()))
//                        .toUri()
//                        .toString())
//                .collect(Collectors.toList());
//    }

}
