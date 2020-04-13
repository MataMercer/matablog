package com.example.microblog.repositories;

import com.example.microblog.models.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {
    LoginToken findBySeries(String seriesId);
    LoginToken findByUsername(String username);
}
