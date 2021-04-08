package com.matamercer.microblog.unit.repository;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.LoginToken;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.LoginTokenRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LoginTokenRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoginTokenRepository loginTokenRepository;

    private User user;
    private LoginToken loginToken;

    @BeforeEach
    public void initData(){
        user = new User("username@gmail.com",
                "username",
                "password",
                true,
                true,
                true,
                true,
                AuthenticationProvider.LOCAL);
        user = entityManager.persist(user);

        loginToken = new LoginToken("series", user, "token", new Date());
        loginToken = entityManager.persist(loginToken);
    }

    @Test
    public void whenFindBySeries_returnLoginToken() {
        LoginToken foundLoginToken = loginTokenRepository.findBySeries(loginToken.getSeries());
        assertThat(foundLoginToken).isEqualTo(loginToken);
    }

    @Test
    public void whenFindByUsername_returnLoginToken() {
        LoginToken foundLoginToken = loginTokenRepository.findByUsername(loginToken.getUser().getUsername());
        assertThat(foundLoginToken).isEqualTo(loginToken);
    }
}