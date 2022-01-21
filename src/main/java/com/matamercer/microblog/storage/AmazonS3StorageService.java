package com.matamercer.microblog.storage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service("amazonS3Storage")
public class AmazonS3StorageService implements StorageService{

    private final AmazonS3 s3;

    @Autowired
    public AmazonS3StorageService(AmazonS3 s3){
        this.s3 = s3;
    }

    @Override
    public void init() {

    }

    @Override
    public void store(Path fileDestPath, MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        extractMetadata(file).forEach(metadata::addUserMetadata);
        try{
            s3.putObject(fileDestPath.toString(), filename, file.getInputStream(), metadata);
        }catch (AmazonServiceException | IOException e){
            throw new IllegalStateException("Failed to store file " + filename + " to Amazon S3" , e);
        }
    }


    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(Path filePath) {
        try{
            S3Object object = s3.getObject(filePath.getParent().toString(), filePath.getFileName().toString());
            byte[] fileBytes = IOUtils.toByteArray(object.getObjectContent());
            return new ByteArrayResource(fileBytes);

        }catch(AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to load file " + filePath.getFileName() + " to Amazon S3" , e);
        }
    }

    //TODO implement this
    @Override
    public void delete(Path filePath) {

    }

    //TODO implement this
    @Override
    public void deleteAll() {

    }

    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }
}
