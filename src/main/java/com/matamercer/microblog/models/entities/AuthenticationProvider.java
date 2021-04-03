package com.matamercer.microblog.models.entities;

import lombok.Getter;

public enum AuthenticationProvider {
    LOCAL("local"),
    OKTA("okta"),
    GITHUB("github");

    @Getter
    private final String provider;

    private AuthenticationProvider(String provider){
        this.provider = provider;
    }


}
