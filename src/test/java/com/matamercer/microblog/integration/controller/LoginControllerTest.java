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
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.*;

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


    @BeforeEach
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

    private RequestEntity<LoginUserForm> getLoginRequestEntity(LoginUserForm loginUserForm) throws UnknownHostException {
        return RequestEntity
                .post(URI.create(environmentUtil.getServerUrl()+ "/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginUserForm);
    }

    @Test
    public void whenLogin_withGoodCredentials_thenReturnResponseIsOk_WithAccessAndRefreshTokens() throws UnknownHostException {
        val requestEntity = getLoginRequestEntity(new LoginUserForm(user.getUsername(), unencodedPassword));
        val responseEntity = testRestTemplate.exchange(requestEntity, Object.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        var accessToken = responseEntity.getHeaders().get(jwtConfig.getAuthorizationHeader()).stream().findFirst();
        assertThat(accessToken.isPresent()).isTrue();
        var refreshToken = responseEntity.getHeaders().get(jwtConfig.getRefreshTokenHeader()).stream().findFirst();
        assertThat(refreshToken.isPresent()).isTrue();
    }
}
