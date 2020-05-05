package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.Authority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}