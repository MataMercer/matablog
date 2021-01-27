package com.matamercer.microblog.services;

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
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SecretKey secretKey;



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

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    private boolean usernameExists(final String username) {
        return userRepository.findByUsername(username) != null;
    }
}
