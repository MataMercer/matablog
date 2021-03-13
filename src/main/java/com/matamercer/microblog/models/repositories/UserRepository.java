package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByoAuth2IdAndAuthenticationProvider(String oAuth2Id, AuthenticationProvider authenticationProvider);
}
