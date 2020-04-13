package com.example.microblog;

import com.example.microblog.configuration.SeederConfig;
import com.example.microblog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MicroblogApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicroblogApplication.class, args);
	}

}
