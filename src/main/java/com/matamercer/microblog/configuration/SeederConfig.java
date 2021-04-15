package com.matamercer.microblog.configuration;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.AuthorityRepository;
import com.matamercer.microblog.models.repositories.UserRepository;

import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeederConfig {
    @Bean
    public CommandLineRunner seedData(UserRepository userRepository,
                                      AuthorityRepository authorityRepository,
                                      PasswordEncoder passwordEncoder,
                                      UserService userService,
                                      PostService postService) {
        return (args) -> {
            User adminUser = new User(
                    "developer.mercer@gmail.com",
                    "a",
                    passwordEncoder.encode("1"),
                    true,
                    true,
                    true,
                    true,
                    AuthenticationProvider.LOCAL
            );
            User foundUser = userRepository.findByUsername("adminuser");
            if(foundUser == null){
                userService.createUser(adminUser, UserRole.ADMIN);
            }
        };
    }
}
