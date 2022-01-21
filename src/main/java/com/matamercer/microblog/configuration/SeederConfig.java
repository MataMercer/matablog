package com.matamercer.microblog.configuration;

import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserRepository;

import com.matamercer.microblog.security.authorization.UserRole;
import com.matamercer.microblog.services.BlogService;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.UserService;
import lombok.var;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeederConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final PostService postService;
    private final BlogService blogService;

    public SeederConfig(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService, PostService postService, BlogService blogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.postService = postService;
        this.blogService = blogService;
    }

    @Bean
    public CommandLineRunner seedData() {
        return (args) -> {
            User adminUser = new User(
                    "developer.mercer@gmail.com",
                    "a",
                    passwordEncoder.encode("1"),
                    UserRole.ADMIN,
                    true,
                    true,
                    true,
                    true,
                    AuthenticationProvider.LOCAL
            );
            addUserToApp(adminUser);

            User bloggerUser = new User(
                    "mercer233@gmail.com",
                    "b",
                    passwordEncoder.encode("1"),
                    UserRole.BLOGGER,
                    true,
                    true,
                    true,
                    true,
                    AuthenticationProvider.LOCAL
            );
            addUserToApp(bloggerUser);

            User readerUser = new User(
                    "mercer233@googlemail.com",
                    "c",
                    passwordEncoder.encode("1"),
                    UserRole.READER,
                    true,
                    true,
                    true,
                    true,
                    AuthenticationProvider.LOCAL
            );
            addUserToApp(readerUser);
        };
    }

    private void addUserToApp(User user){
        var foundUser = userRepository.findByEmail(user.getEmail());
        if(!foundUser.isPresent()){
            User userEntity = userService.createUser(user);
            blogService.createDefaultBlogForUser(userEntity);
        }
    }
}
