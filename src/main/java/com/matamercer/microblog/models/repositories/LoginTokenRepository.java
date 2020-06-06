package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {
    LoginToken findBySeries(String seriesId);

    @Query("SELECT l FROM LoginToken l INNER JOIN l.user u WHERE u.username = :username")
    LoginToken findByUsername(@Param("username") String username);
}
