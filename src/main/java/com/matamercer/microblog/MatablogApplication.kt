package com.matamercer.microblog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [RedisRepositoriesAutoConfiguration::class]) //@EnableCaching
class MatablogApplication

fun main(args: Array<String>) {
    runApplication<MatablogApplication>(*args)
}