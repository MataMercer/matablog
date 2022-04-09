package com.matamercer.microblog.storage

import com.matamercer.microblog.configuration.StorageConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service("fileSystemStorage")
class FileSystemStorageService @Autowired constructor(properties: StorageConfig) : StorageService {
    private val rootLocation: Path

    init {
        rootLocation = Paths.get(properties.location)
    }

    override fun store(fileDestPath: Path, file: MultipartFile) {
        val filename = StringUtils.cleanPath(file.originalFilename)
        try {
            if (file.isEmpty) {
                throw StorageException("Failed to store empty file $filename")
            }
            if (filename.contains("..")) {
                // This is a security check
                throw StorageException(
                    "Cannot store file with relative path outside current directory "
                            + filename
                )
            }
            file.inputStream.use { inputStream ->
                var combinedFileDestPath = rootLocation.resolve(fileDestPath)
                combinedFileDestPath = Files.createDirectory(combinedFileDestPath)
                Files.copy(
                    inputStream, combinedFileDestPath.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
        } catch (e: IOException) {
            throw StorageException("Failed to store file $filename", e)
        }
    }

    override fun loadAsResource(filePath: Path): Resource {
        return try {
            val resource: Resource = UrlResource(rootLocation.resolve(filePath).toUri())
            if (resource.exists() || resource.isReadable) {
                resource
            } else {
                throw StorageFileNotFoundException(
                    "Could not read file: " + filePath.fileName
                )
            }
        } catch (e: MalformedURLException) {
            throw StorageFileNotFoundException("Could not read file: " + filePath.fileName, e)
        }
    }

    override fun delete(filePath: Path) {
        try {
            FileSystemUtils.deleteRecursively(rootLocation.resolve(filePath).toAbsolutePath().parent)

        } catch (e: IOException) {
            throw StorageFileNotFoundException("Could not read file: " + filePath.fileName, e)
        }
    }

    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }

    override fun init() {
        try {
            Files.createDirectories(rootLocation)
        } catch (e: IOException) {
            throw StorageException("Could not initialize storage", e)
        }
    }
}