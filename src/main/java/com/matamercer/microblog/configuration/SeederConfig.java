package com.matamercer.microblog.configuration;

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

            //Admin account
            User adminUser = new User(
                    "aurocomm@googlemail.com",
                    "a",
                    passwordEncoder.encode("1"),
                    true,
                    true,
                    true,
                    true
            );
            User foundUser = userRepository.findByUsername("adminuser");
            if(foundUser == null){
//
//                userRepository.save(adminUser);
//                Set<String> authorities = UserRole.ADMIN.getGrantedAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());;;
//                for(String authority: authorities){
//                    authorityRepository.save(new Authority(authority, adminUser));
//                }

                userService.createUser(adminUser, UserRole.ADMIN);

//                for(int i = 0; i< 2; i++){
//                    Post post = new Post();
//                    post.setContent("Test Post " + i);
//                    post.setBlog(userRepository.findByUsername(adminUser.getUsername()).getActiveBlog());
//                    postService.createPost(post);
//                }




            }





            // save a couple of customers
//            repository.save(new Customer("Jack", "Bauer"));
//            repository.save(new Customer("Chloe", "O'Brian"));
//            repository.save(new Customer("Kim", "Bauer"));
//            repository.save(new Customer("David", "Palmer"));
//            repository.save(new Customer("Michelle", "Dessler"));
//
//            // fetch all customers
//            log.info("Customers found with findAll():");
//            log.info("-------------------------------");
//            for (Customer customer : repository.findAll()) {
//                log.info(customer.toString());
//            }
//            log.info("");
//
//            // fetch an individual customer by ID
//            Customer customer = repository.findOne(1L);
//            log.info("Customer found with findOne(1L):");
//            log.info("--------------------------------");
//            log.info(customer.toString());
//            log.info("");
//
//            // fetch customers by last name
//            log.info("Customer found with findByLastNameStartsWithIgnoreCase('Bauer'):");
//            log.info("--------------------------------------------");
//            for (Customer bauer : repository
//                    .findByLastNameStartsWithIgnoreCase("Bauer")) {
//                log.info(bauer.toString());
//            }
//            log.info("");
        };
    }
}
