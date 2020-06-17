package com.matamercer.microblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
		exclude = { RedisRepositoriesAutoConfiguration.class }
)
@EnableCaching
public class MicroblogApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicroblogApplication.class, args);
	}

}
