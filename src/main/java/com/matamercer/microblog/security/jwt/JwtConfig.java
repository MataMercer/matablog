package com.matamercer.microblog.security.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {
    private String secretKey;
    private String tokenPrefix;
    private Integer refreshTokenExpirationInDays;
    private Integer accessTokenExpirationInHours;

    public String getAuthorizationHeader(){
        return HttpHeaders.AUTHORIZATION;
    }

    public String getRefreshTokenHeader(){
        return "refreshToken";
    }




}
