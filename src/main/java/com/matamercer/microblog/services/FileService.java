package com.matamercer.microblog.services;

import com.matamercer.microblog.Exceptions.NotFoundException;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.File;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.repositories.FileRepository;
import com.matamercer.microblog.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final StorageService storageService;
    @Autowired
    public FileService(FileRepository fileRepository, @Qualifier("fileSystemStorage") StorageService storageService){
        this.fileRepository = fileRepository;
        this.storageService = storageService;
    }

    @PreAuthorize("hasAuthority(T(com.matamercer.microblog.security.authorization.UserAuthority).FILE_CREATE.toString())")
    public File createFile(MultipartFile multipartFile, Blog blog){
        File file = new File();
        file.setName(StringUtils.cleanPath(multipartFile.getOriginalFilename()));
        file = fileRepository.save(file);
        //store in a folder named after the file's id. This is because there is no guarantee the filename is unique while also preserving filename.
        storageService.store(Paths.get(file.getId().toString()), multipartFile);
        return file;
    }

    public File getFile(long fileId){
        log.debug("Get file " + fileId);

        Optional<File> file = fileRepository.findById(fileId);

        if (!file.isPresent()) {
            throw new NotFoundException("File with id " + fileId + " is not found.");
        }else{
            return file.get();
        }
    }

    public Resource getResourceFromFile(File file){
        return storageService.loadAsResource(getPath(file));
    }


    @PreAuthorize("hasAuthority(T(com.matamercer.microblog.security.authorization.UserAuthority).FILE_UPDATE.toString())")
    public void deleteFile(File file){
        fileRepository.delete(file);
        storageService.delete(getPath(file));
    }

    private Path getPath(File file){
        return Paths.get(file.getId() + "\\" + file.getName());
    }


}
