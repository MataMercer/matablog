package com.matamercer.microblog.storage

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.util.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.name

@Service("amazonS3Storage")
class AmazonS3StorageService @Autowired constructor(private val s3: AmazonS3) : StorageService {
    override fun init() {}
    override fun store(fileDestPath: Path, file: MultipartFile) {
        val filename = StringUtils.cleanPath(file.originalFilename!!)
        val metadata = ObjectMetadata()
        extractMetadata(file).forEach { (key: String?, value: String?) -> metadata.addUserMetadata(key, value) }
        try {
            s3.putObject(fileDestPath.toString(), filename, file.inputStream, metadata)
        } catch (e: AmazonServiceException) {
            throw IllegalStateException("Failed to store file $filename to Amazon S3", e)
        } catch (e: IOException) {
            throw IllegalStateException("Failed to store file $filename to Amazon S3", e)
        }
    }

    fun load(filename: String?): Path? {
        return null
    }

    override fun loadAsResource(filePath: Path): Resource {
        return try {
            val `object` = s3.getObject(filePath.parent.toString(), filePath.fileName.toString())
            val fileBytes = IOUtils.toByteArray(`object`.objectContent)
            ByteArrayResource(fileBytes)
        } catch (e: AmazonServiceException) {
            throw IllegalStateException("Failed to load file " + filePath.fileName + " to Amazon S3", e)
        } catch (e: IOException) {
            throw IllegalStateException("Failed to load file " + filePath.fileName + " to Amazon S3", e)
        }
    }

    //TODO check implement this
    override fun delete(filePath: Path) {
        try{
            s3.deleteObject(filePath.parent.toString(), filePath.fileName.toString())
        } catch (e: AmazonServiceException) {
            throw IllegalStateException("Failed to delete file " + filePath.fileName + " from Amazon S3.", e)
        }
    }

    //TODO implement this
    override fun deleteAll() {}
    private fun extractMetadata(file: MultipartFile): Map<String, String> {
        val metadata: MutableMap<String, String> = HashMap()
        metadata["Content-Type"] = file.contentType!!
        metadata["Content-Length"] = file.size.toString()
        return metadata
    }
}