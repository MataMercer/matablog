package com.matamercer.microblog.integration.controller;

import com.matamercer.microblog.forms.LoginUserForm;
import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.security.jwt.JwtConfig;
import com.matamercer.microblog.security.jwt.JwtUtil;
import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.utilities.EnvironmentUtil;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private EnvironmentUtil environmentUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtConfig jwtConfig;

    private TestRestTemplate testRestTemplate;

    private User user;

    private String unencodedPassword;



    @Before
    public void initData(){
        unencodedPassword = "password";
        user = new User("username@gmail.com",
                "username",
                passwordEncoder.encode(unencodedPassword),
                UserRole.USER,
                true,
                true,
                true,
                true,
                AuthenticationProvider.LOCAL);
        user = userService.createUser(user);
        testRestTemplate = new TestRestTemplate();
    }

    @After
    public void disposeData(){
        userRepository.deleteById(user.getId());
    }


    private RequestEntity<LoginUserForm> getLoginRequestEntity(LoginUserForm loginUserForm) throws UnknownHostException {
        return RequestEntity
                .post(URI.create(environmentUtil.getServerUrl()+ "/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginUserForm);
    }

    private RequestEntity<Void> getCurrentUserRequestEntity(String accessToken) throws UnknownHostException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(jwtConfig.getAuthorizationHeader(), accessToken);
        return RequestEntity.get(URI.create(environmentUtil.getServerUrl() + "/currentuser"))
                .headers(headers)
                .build();
    }


    @Test
    public void whenLogin_withGoodCredentials_thenReturnResponseIsOk_WithAccessAndRefreshTokens() throws UnknownHostException {
        val requestEntity = getLoginRequestEntity(new LoginUserForm(user.getUsername(), unencodedPassword));
        val responseEntity = testRestTemplate.exchange(requestEntity, Object.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        val accessToken = responseEntity.getHeaders().get(jwtConfig.getAuthorizationHeader()).stream().findFirst();
        assertThat(accessToken.isPresent()).isTrue();
        val refreshToken = responseEntity.getHeaders().get(jwtConfig.getRefreshTokenHeader()).stream().findFirst();
        assertThat(refreshToken.isPresent()).isTrue();
    }

    @Test
    public void whenGetCurrentUser_withAccessToken_thenReturnCurrentUser() throws UnknownHostException {
        val requestEntity = getLoginRequestEntity(new LoginUserForm(user.getUsername(), unencodedPassword));
        val responseEntity = testRestTemplate.exchange(requestEntity, Object.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        val accessToken = responseEntity.getHeaders().get(jwtConfig.getAuthorizationHeader()).stream().findFirst();
        assertThat(accessToken.isPresent()).isTrue();
        val refreshToken = responseEntity.getHeaders().get(jwtConfig.getRefreshTokenHeader()).stream().findFirst();
        assertThat(refreshToken.isPresent()).isTrue();

        val currentUserRequestEntity = getCurrentUserRequestEntity(accessToken.get());
        val currentUserResponseEntity= testRestTemplate.exchange(currentUserRequestEntity, String.class);

        assertThat(currentUserResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void whenGetCurrentUser_withoutAccessToken_thenReturnCurrentUser() throws UnknownHostException {

        val currentUserRequestEntity = getCurrentUserRequestEntity("");
        val currentUserResponseEntity= testRestTemplate.exchange(currentUserRequestEntity, String.class);

        assertThat(currentUserResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
