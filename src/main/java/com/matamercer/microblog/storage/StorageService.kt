package com.matamercer.microblog.storage

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface StorageService {
    fun init()
    fun store(fileDestPath: Path, file: MultipartFile)
    fun loadAsResource(filePath: Path): Resource
    fun delete(filePath: Path)
    fun deleteAll()
}