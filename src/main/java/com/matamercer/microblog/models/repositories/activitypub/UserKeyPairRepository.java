package com.matamercer.microblog.models.repositories.activitypub;


import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.entities.activitypub.UserKeyPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserKeyPairRepository extends JpaRepository<UserKeyPair, Long> {
    UserKeyPair findByUser(User user);
}
