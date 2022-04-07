package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.File
import org.springframework.data.jpa.repository.JpaRepository


interface FileRepository : JpaRepository<File, Long>