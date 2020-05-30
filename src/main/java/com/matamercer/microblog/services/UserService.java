package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.Authority;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.entities.activitypub.UserKeyPair;
import com.matamercer.microblog.models.repositories.AuthorityRepository;
import com.matamercer.microblog.models.repositories.activitypub.UserKeyPairRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserKeyPairRepository userKeyPairRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorityRepository authorityRepository;


    @Transactional
    public void createUser(User user, UserRole userRole){
        try{
            userRepository.save(user);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); KeyPair keyPair = keyPairGenerator.generateKeyPair();
            UserKeyPair userKeyPair = new UserKeyPair();
            userKeyPair.setUser(user);
            userKeyPair.setPrivateKey(Base64.getMimeEncoder().encodeToString( keyPair.getPrivate().getEncoded()));
            userKeyPair.setPublicKey(Base64.getMimeEncoder().encodeToString( keyPair.getPublic().getEncoded()));
            userKeyPairRepository.save(userKeyPair);

            Set<String> authorities = userRole.getGrantedAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());;;
            for(String authority: authorities){
                authorityRepository.save(new Authority(authority, user));
            }


        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }





    }



    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        UserBuilder builder = null;
        if (user != null) {
            builder = org.springframework.security.core.userdetails.User.withUsername(username);
            builder.disabled(!user.isEnabled());
            builder.password(user.getPassword());
            String[] authorities = user.getAuthorities()
                    .stream().map(a -> a.getAuthority()).toArray(String[]::new);
            builder.authorities(authorities);
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
        return builder.build();
    }



//    public User createUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
}
