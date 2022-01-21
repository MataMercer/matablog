package com.matamercer.microblog.services;

import com.matamercer.microblog.security.authentication.JwtUtil;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.entities.VerificationToken;
import com.matamercer.microblog.models.repositories.VerificationTokenRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.models.repositories.*;
import com.matamercer.microblog.security.authorization.UserRole;
import com.matamercer.microblog.web.api.v1.dto.mappers.response.UserResponseDtoMapper;
import com.matamercer.microblog.web.api.v1.dto.responses.UserResponseDto;
import com.matamercer.microblog.web.error.exceptions.RevokedRefreshTokenException;
import com.matamercer.microblog.web.error.exceptions.UserAlreadyExistsException;
import com.matamercer.microblog.web.error.exceptions.UserNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.security.*;
import java.util.UUID;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserKeyPairService userKeyPairService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecretKey secretKey;
    private final JwtUtil jwtUtil;
    private final UserResponseDtoMapper userResponseDtoMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       UserKeyPairService userKeyPairService,
                       RefreshTokenRepository refreshTokenRepository,
                       SecretKey secretKey,
                       JwtUtil jwtUtil, UserResponseDtoMapper userResponseDtoMapper) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userKeyPairService = userKeyPairService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.secretKey = secretKey;
        this.jwtUtil = jwtUtil;
        this.userResponseDtoMapper = userResponseDtoMapper;
    }

    @Transactional
    public User createUser(User user) {
        if (emailExists(user.getEmail()) || usernameExists(user.getUsername())) {
            throw new UserAlreadyExistsException(("There is already an account with that email."));
        }


        User registeredUser = userRepository.save(user);

        try {
            userKeyPairService.createUserKeyPairForUser(user);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return registeredUser;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var optionalUser = userRepository.findByUsername(username);
        UserBuilder builder = null;
        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            builder = org.springframework.security.core.userdetails.User.withUsername(username);
            builder.disabled(!user.isEnabled());
            builder.password(user.getPassword());
            String[] authorities = user.getAuthorities().stream().map(SimpleGrantedAuthority::getAuthority).toArray(String[]::new);
            builder.authorities(authorities);
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
        return builder.build();
    }

    @Transactional
    public String grantAccessToken(String refreshToken){
            Jws<Claims> claimsJws = Jwts
                    .parserBuilder().setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken);

            var body = claimsJws.getBody();
            var userId = Long.parseLong((String) body.get("userId"));
            var userRole = UserRole.valueOf((String) body.get("userRole"));
            var refreshTokenId = Long.parseLong((String) body.get("refreshTokenEntityId"));

            var persistedRefreshToken = refreshTokenRepository.findById(refreshTokenId);
            if(persistedRefreshToken.isPresent()){
                return jwtUtil.createAccessToken(userId);
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

    public VerificationToken getVerificationToken(final String VerificationToken) {
        return verificationTokenRepository.findByToken(VerificationToken);
    }

    public User getUser(final VerificationToken verificationToken) {
        final VerificationToken token = verificationTokenRepository.findByToken(verificationToken.getToken());
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    public UserResponseDto getUser(long id){
        var optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent()){
            throw new UserNotFoundException("Unable to create post because unable to find logged in user.");
        }
        return userResponseDtoMapper.map(optionalUser.get());
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public void delete(User user){
        userRepository.delete(user);
    }

    public boolean emailExists(final String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean usernameExists(final String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
