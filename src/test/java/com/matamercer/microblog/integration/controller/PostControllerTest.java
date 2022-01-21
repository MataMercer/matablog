package com.matamercer.microblog.integration.controller;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.security.authorization.UserRole;
import com.matamercer.microblog.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PostControllerTest {

    private User user;

    @Autowired
    private UserService userService;

    private TestRestTemplate testRestTemplate;

    @Before
    public void initData(){
        user = new User("username@gmail.com",
                "username",
                "",
                UserRole.BLOGGER,
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
        userService.delete(user);
    }

    @Test
    public void createPost_thenReturnOk(){

    }
}
