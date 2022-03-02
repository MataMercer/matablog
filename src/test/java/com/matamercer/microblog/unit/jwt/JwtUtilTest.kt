package com.matamercer.microblog.unit.jwt;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.RefreshToken;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.RefreshTokenRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.authorization.UserRole;
import com.matamercer.microblog.security.authentication.JwtConfig;
import com.matamercer.microblog.security.authentication.JwtSecretKey;
import com.matamercer.microblog.security.authentication.JwtUtil;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import javax.crypto.SecretKey;

@RunWith(SpringRunner.class)
@TestPropertySource("/application.properties")
@ActiveProfiles("test")
public class JwtUtilTest {

    @Value("${application.jwt.secretKey}")
    private String jwtConfigSecretKey;
    @Value("${application.jwt.tokenPrefix}")
    private String jwtConfigTokenPrefix;
    @Value("${application.jwt.refreshTokenExpirationInDays}")
    private Integer jwtConfigRefreshTokenExpirationInDays;
    @Value("${application.jwt.accessTokenExpirationInHours}")
    private Integer getJwtConfigAccessTokenExpirationInHours;


    private User user;
    private JwtUtil jwtUtil;

    @Before
    public void setup(){
        user = new User("username@gmail.com",
                "username",
                "password",
                UserRole.BLOGGER,
                true,
                true,
                true,
                true,
                AuthenticationProvider.LOCAL);
        user.setId(0L);
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecretKey(jwtConfigSecretKey);
        jwtConfig.setTokenPrefix(jwtConfigTokenPrefix);
        jwtConfig.setRefreshTokenExpirationInDays(jwtConfigRefreshTokenExpirationInDays);
        jwtConfig.setAccessTokenExpirationInHours(getJwtConfigAccessTokenExpirationInHours);

        JwtSecretKey jwtSecretKey = new JwtSecretKey(jwtConfig);
        SecretKey secretKey = jwtSecretKey.getSecretKey();

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(java.util.Optional.ofNullable(user));

        RefreshToken refreshToken = new RefreshToken(user);
        refreshToken.setId(0L);
        RefreshTokenRepository refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        Mockito.when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(refreshToken);

        jwtUtil = new JwtUtil(secretKey, jwtConfig, userRepository, refreshTokenRepository);
    }


    @Test
    public void whenCreateAccessToken_returnValidAccessToken(){
        String token = jwtUtil.createAccessToken(user.getId());
        val claims = jwtUtil.extractAllClaims(token);
        assertThat(jwtUtil.isTokenExpired(claims)).isFalse();
        assertThat(jwtUtil.getUserId(claims)).isEqualTo(user.getId());
        assertThat(jwtUtil.getUserName(claims)).isEqualTo(user.getUsername());
        assertThat(jwtUtil.getUserRole(claims)).isEqualTo(user.getRole());
    }

    @Test
    public void whenCreateRefreshToken_returnValidRefreshToken(){
        String token = jwtUtil.createRefreshToken(user.getId());
        val claims = jwtUtil.extractAllClaims(token);
        assertThat(jwtUtil.isTokenExpired(claims)).isFalse();
        assertThat(jwtUtil.getUserId(claims)).isEqualTo(user.getId());
        assertThat(jwtUtil.getUserName(claims)).isEqualTo(user.getUsername());
        assertThat(jwtUtil.getUserRole(claims)).isEqualTo(user.getRole());   assertThat(jwtUtil.isTokenExpired(claims)).isFalse();
    }
}
