package com.matamercer.microblog.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(Path fileDestPath, MultipartFile file);

    Resource loadAsResource(Path filePath);



    void deleteAll();
}
