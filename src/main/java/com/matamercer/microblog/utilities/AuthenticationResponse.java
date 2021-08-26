package com.matamercer.microblog.utilities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;

    public AuthenticationResponse(String token) {
        super();
        this.token = token;
    }
}
