package com.matamercer.microblog.security.jwt;

import com.matamercer.microblog.models.entities.RefreshToken;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.RefreshTokenRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.web.error.AuthenticationException;
import com.matamercer.microblog.web.error.UserNotFoundException;
import io.jsonwebtoken.*;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    private String createToken(Map<String, ?> claims, Date exp){
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .signWith(secretKey)
                .compact();
    }

    private String createRefreshTokenHelper(Optional<User> optionalUser){
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            RefreshToken refreshTokenEntity = refreshTokenRepository.save(new RefreshToken(user));
            var claims = getUserClaims(user.getId(), user.getUsername(), user.getRole());
            claims.put("refreshTokenEntityId", refreshTokenEntity.getId().toString());
            return createToken(claims, java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getRefreshTokenExpirationInDays())));
        }else{
            throw new UserNotFoundException("Error creating refresh token. User not found.");
        }
    }

    private Map<String, String> getUserClaims(Long userId, String username, UserRole userRole ){
        Map<String, String> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("username", username);
        claims.put("userRole", userRole.toString());
        return claims;
    }

    private Date addHoursToCurrentDate(int hours){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public Claims extractAllClaims(String token){
        try {
            return Jwts
                    .parserBuilder().setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token).getBody();
        } catch(ExpiredJwtException expiredJwtException) {
            throw new AuthenticationException("Token is expired", expiredJwtException);
        } catch(UnsupportedJwtException unsupportedJwtException) {
            throw new AuthenticationException("This JWT token is unsupported", unsupportedJwtException);
        } catch(MalformedJwtException malformedJwtException) {
            throw new AuthenticationException("Malformed JWT", malformedJwtException);
        } catch(SignatureException signatureException) {
            throw new AuthenticationException("JWT Signature invalid", signatureException);
        } catch(IllegalArgumentException illegalArgumentException) {
            throw new AuthenticationException("Illegal argument", illegalArgumentException);
        }
    }

    public boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }

    public Long getUserId(Claims claims){
        return Long.parseLong(claims.get("userId").toString());
    }

    public UserRole getUserRole(Claims claims){
        return UserRole.valueOf(claims.get("userRole").toString());
    }

    public String getUserName(Claims claims){
        return claims.get("username").toString();
    }






}
