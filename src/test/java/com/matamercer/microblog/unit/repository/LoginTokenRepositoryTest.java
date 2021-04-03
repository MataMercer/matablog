package com.matamercer.microblog.unit.repository;

import com.matamercer.microblog.models.entities.LoginToken;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.LoginTokenRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
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

    @Test
    void whenFindBySeries_returnLoginToken() {
        User user = new User();
        entityManager.persist(user);
        LoginToken loginToken = new LoginToken("series", user, "token", new Date());
        loginToken = entityManager.persist(loginToken);
        LoginToken foundLoginToken = loginTokenRepository.findBySeries(loginToken.getSeries());
        assertThat(foundLoginToken).isEqualTo(loginToken);
    }

    @Test
    void whenFindByUsername_returnLoginToken() {
        User user = new User();
        user.setUsername("username");
        entityManager.persist(user);
        LoginToken loginToken = new LoginToken("series", user, "token", new Date());
        loginToken = entityManager.persist(loginToken);
        LoginToken foundLoginToken = loginTokenRepository.findByUsername(loginToken.getUser().getUsername());
        assertThat(foundLoginToken).isEqualTo(loginToken);
    }
}