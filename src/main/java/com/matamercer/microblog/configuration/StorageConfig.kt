package com.matamercer.microblog.configuration

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("storage")
class StorageConfig {
    //FileSystem Settings
    var location = "upload-dir\\"

    //AWS3 Settings
    @Bean
    fun s3(): AmazonS3 {
        val awsCredentials: AWSCredentials = BasicAWSCredentials(
            "",
            ""
        )
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(Regions.US_WEST_1)
            .build()
    }
}