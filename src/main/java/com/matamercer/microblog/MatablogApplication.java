package com.matamercer.microblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;

@SpringBootApplication(
		exclude = { RedisRepositoriesAutoConfiguration.class }
)
//@EnableCaching
public class MatablogApplication {
	public static void main(String[] args) {
		SpringApplication.run(MatablogApplication.class, args);
	}

}
