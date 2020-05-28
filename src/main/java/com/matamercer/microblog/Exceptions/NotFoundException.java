package com.matamercer.microblog.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class NotFoundException extends RuntimeException{
    private String message;

    public NotFoundException(){

    }

    public NotFoundException(String message){
        this.message = message;
    }
}
