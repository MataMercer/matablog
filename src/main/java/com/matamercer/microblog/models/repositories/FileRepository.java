package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository  extends JpaRepository<File, Long> {

}
