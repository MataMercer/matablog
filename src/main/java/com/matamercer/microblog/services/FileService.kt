package com.matamercer.microblog.services

import com.matamercer.microblog.Exceptions.NotFoundException
import com.matamercer.microblog.models.entities.*
import com.matamercer.microblog.models.repositories.FileRepository
import com.matamercer.microblog.storage.StorageService
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.Resource
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.nio.file.Paths

@Service
@Transactional
@Slf4j
class FileService @Autowired constructor(
    private val fileRepository: FileRepository, @param:Qualifier(
        "fileSystemStorage"
    ) private val storageService: StorageService
) {
    @PreAuthorize("hasAuthority(T(com.matamercer.microblog.security.authorization.UserAuthority).FILE_CREATE.toString())")
    fun createFile(multipartFile: MultipartFile, blog: Blog?): File {
        var file = File()
        file.name = StringUtils.cleanPath(multipartFile.originalFilename)
        file = fileRepository.save(file)
        storageService.store(Paths.get(file.id.toString()), multipartFile)
        return file
    }

    fun getFile(fileId: Long): File {
        val file = fileRepository.findById(fileId)
        return if (!file.isPresent) {
            throw NotFoundException("File with id $fileId is not found.")
        } else {
            file.get()
        }
    }

    fun getResourceFromFile(file: File): Resource {
        return storageService.loadAsResource(getPath(file))
    }

    @PreAuthorize("hasAuthority(T(com.matamercer.microblog.security.authorization.UserAuthority).FILE_UPDATE.toString())")
    fun deleteFile(file: File) {
        fileRepository.delete(file)
        storageService.delete(getPath(file))
    }

    private fun getPath(file: File): Path {
        return Paths.get(file.id.toString() + "\\" + file.name)
    }
}