package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {
    LoginToken findBySeries(String seriesId);
    LoginToken findByUsername(String username);
}
