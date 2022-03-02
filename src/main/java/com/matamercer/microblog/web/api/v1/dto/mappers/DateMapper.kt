package com.matamercer.microblog.web.api.v1.dto.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public class DateMapper {
    public String asString(ZonedDateTime zonedDateTime){
        if(zonedDateTime == null){
            return null;
        }
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
