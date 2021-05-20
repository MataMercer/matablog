package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByoAuth2IdAndAuthenticationProvider(String oAuth2Id, AuthenticationProvider authenticationProvider);
}
