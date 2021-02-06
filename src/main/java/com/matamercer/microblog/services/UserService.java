package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.Authority;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.entities.VerificationToken;
import com.matamercer.microblog.models.entities.activitypub.UserKeyPair;
import com.matamercer.microblog.models.repositories.AuthorityRepository;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.VerificationTokenRepository;
import com.matamercer.microblog.models.repositories.activitypub.UserKeyPairRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.web.error.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserKeyPairRepository userKeyPairRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Transactional
    public User createUser(User user, UserRole userRole) {
        if (emailExists(user.getEmail()) || usernameExists(user.getUsername())) {
            throw new UserAlreadyExistsException(("There is already an account with that email."));
        }

        try {
            // add a default blog to user, so they can add posts to it.
            Blog blog = new Blog();
            // by default, the default blog will have just the user's username. this can be
            // changed by the user later on.
            blog.setBlogname(user.getUsername());
            blog.setPreferredBlogName(user.getUsername());
            blog.setSensitive(false);
            blogRepository.save(blog);
            user.addBlog(blog);
            user.setActiveBlog(blog);
            User registeredUser = userRepository.save(user);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            UserKeyPair userKeyPair = new UserKeyPair();
            userKeyPair.setUser(user);
            userKeyPair.setPrivateKey(Base64.getMimeEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
            userKeyPair.setPublicKey(Base64.getMimeEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            userKeyPairRepository.save(userKeyPair);

            Set<String> authorities = userRole.getGrantedAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            ;
            ;
            for (String authority : authorities) {
                authorityRepository.save(new Authority(authority, user));
            }
            return registeredUser;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
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
            String[] authorities = user.getAuthorities().stream().map(Authority::getAuthority).toArray(String[]::new);
            builder.authorities(authorities);
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
        return builder.build();
    }

    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(existingVerificationToken);
        verificationToken.updateToken(UUID.randomUUID().toString());
        verificationToken = verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    public VerificationToken getVerificationToken(String VerificationToken) {
        return verificationTokenRepository.findByToken(VerificationToken);
    }

    public User getUser(VerificationToken verificationToken) {
        final VerificationToken token = verificationTokenRepository.findByToken(verificationToken.getToken());
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    public boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    public boolean usernameExists(final String username) {
        return userRepository.findByUsername(username) != null;
    }

}
