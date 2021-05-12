package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.Authority;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.entities.VerificationToken;
import com.matamercer.microblog.models.repositories.AuthorityRepository;
import com.matamercer.microblog.models.repositories.VerificationTokenRepository;
import com.matamercer.microblog.models.repositories.UserKeyPairRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.jwt.JwtUtil;
import com.matamercer.microblog.models.entities.*;
import com.matamercer.microblog.models.entities.activitypub.UserKeyPair;
import com.matamercer.microblog.models.repositories.*;
import com.matamercer.microblog.models.repositories.activitypub.UserKeyPairRepository;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.web.error.RevokedRefreshTokenException;
import com.matamercer.microblog.web.error.UserAlreadyExistsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.security.*;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserKeyPairRepository userKeyPairRepository;
    private final AuthorityRepository authorityRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final BlogService blogService;
    private final UserKeyPairService userKeyPairService;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserKeyPairRepository userKeyPairRepository,
                       AuthorityRepository authorityRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       BlogService blogService,
                       UserKeyPairService userKeyPairService) {
        this.userRepository = userRepository;
        this.userKeyPairRepository = userKeyPairRepository;
        this.authorityRepository = authorityRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.blogService = blogService;
        this.userKeyPairService = userKeyPairService;
    }

    @Transactional
    public User createUser(User user, UserRole userRole) {
        if (emailExists(user.getEmail()) || usernameExists(user.getUsername())) {
            throw new UserAlreadyExistsException(("There is already an account with that email."));
        }

        Blog defaultBlog = blogService.createDefaultBlogForUser(user);
        user.addBlog(defaultBlog);
        user.setActiveBlog(defaultBlog);
        User registeredUser = userRepository.save(user);

        try {
            userKeyPairService.createUserKeyPairForUser(user);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        Set<String> authorities = userRole.getGrantedAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        for (String authority : authorities) {
            authorityRepository.save(new Authority(authority, user));
        }
        return registeredUser;
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



    //Tokens

    public String generateRefreshToken(String username, String accessToken){
        //make a persistent refreshtoken entity in db to look up later.
        User user = userRepository.findByUsername(username);
        RefreshToken persistedRefreshToken = refreshTokenRepository.save(new RefreshToken(user));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setId(persistedRefreshToken.getId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 300000)) //set for 5m
                .signWith(secretKey)
                .compact();

    }

    public String generateAccessToken(String refreshToken){
            //check this refreshtoken is legit
            Jws<Claims> claimsJws = Jwts
                    .parserBuilder().setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken);

            //extract important information from it
            Claims body = claimsJws.getBody();
            String username = body.getSubject();
            Long id = Long.parseLong(body.getId());

            //check db by ID for the refresh token
            var persistedRefreshToken = refreshTokenRepository.findById(id);
            if(persistedRefreshToken.isPresent()){
                return jwtUtil.generateToken((String) username);
            }
            else{
                throw new RevokedRefreshTokenException();
            }
    }


    //Email Activation
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
