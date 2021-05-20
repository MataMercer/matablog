package com.matamercer.microblog.unit.repository;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.UserRole;
import lombok.var;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void initData(){
        user = new User("username@gmail.com",
                "username",
                "password",
                UserRole.USER,
                true,
                true,
                true,
                true,
                AuthenticationProvider.LOCAL);
        user = entityManager.persist(user);
    }

    @AfterEach
    public void flushAfter() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void whenFindByUsername_thenReturnUser() {
        var foundUser = userRepository.findByUsername(user.getUsername());
        assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    public void whenFindByEmail_thenReturnUser() {
        var foundUser = userRepository.findByEmail(user.getEmail());
        assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    public void whenFindByoAuth2IdAndAuthenticationProvider_thenReturnUser() {
        var foundOptionalUser = userRepository.findByoAuth2IdAndAuthenticationProvider(user.getOAuth2Id(),
                user.getAuthenticationProvider());
        assertThat(foundOptionalUser.get()).isEqualTo(user);
    }
}