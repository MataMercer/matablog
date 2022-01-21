package com.matamercer.microblog.web.api.v1.dto.mappers.response;


import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.web.api.v1.dto.responses.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserResponseDtoMapper {
    public abstract UserResponseDto map(User source);
}
