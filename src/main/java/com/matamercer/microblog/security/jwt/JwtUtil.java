package com.matamercer.microblog.security.jwt;

import com.matamercer.microblog.models.entities.RefreshToken;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.RefreshTokenRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.web.error.UserNotFoundException;
import io.jsonwebtoken.*;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtUtil {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public JwtUtil(SecretKey secretKey, JwtConfig jwtConfig, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createToken(Map<String, ?> claims, Date exp){
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .signWith(secretKey)
                .compact();
    }

    @Transactional
    public String createAccessToken(Long userId){
        var optionalUser = userRepository.findById(userId);
        return createAccessTokenHelper(optionalUser);
    }

    @Transactional
    public String createAccessToken(String username){
        var optionalUser = userRepository.findByUsername(username);
        return createAccessTokenHelper(optionalUser);
    }

    private String createAccessTokenHelper(Optional<User> optionalUser){
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            return createToken(getUserClaims(user.getId(), user.getUsername(), user.getRole()), addHoursToCurrentDate(jwtConfig.getAccessTokenExpirationInHours()));
        }else{
            throw new UserNotFoundException("Error creating access token. User not found.");
        }
    }

    @Transactional
    public String createRefreshToken(Long userId){
        var optionalUser = userRepository.findById(userId);
        return createRefreshTokenHelper(optionalUser);
    }

    @Transactional
    public String createRefreshToken(String username){
        var optionalUser = userRepository.findByUsername(username);
        return createRefreshTokenHelper(optionalUser);
    }

    private String createRefreshTokenHelper(Optional<User> optionalUser){
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            RefreshToken refreshTokenEntity = refreshTokenRepository.save(new RefreshToken(user));
            var claims = getUserClaims(user.getId(), user.getUsername(), user.getRole());
            claims.put("refreshTokenEntityId", refreshTokenEntity.toString());
            return createToken(claims, java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getRefreshTokenExpirationInDays())));
        }else{
            throw new UserNotFoundException("Error creating refresh token. User not found.");
        }
    }

    private Map<String, String> getUserClaims(Long userId, String username, UserRole userRole ){
        Map<String, String> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("username", userId.toString());
        claims.put("userRole", userRole.toString());
        return claims;
    }

    private Date addHoursToCurrentDate(int hours){
        LocalDateTime addedDate = LocalDateTime.now().plusHours(hours);
        return java.sql.Date.valueOf(addedDate.toLocalDate());
    }






}
