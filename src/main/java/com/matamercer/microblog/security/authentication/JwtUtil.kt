package com.matamercer.microblog.security.authentication

import com.matamercer.microblog.models.entities.RefreshToken
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.RefreshTokenRepository
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.web.error.exceptions.AuthenticationException
import com.matamercer.microblog.web.error.exceptions.UserNotFoundException
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Date
import java.time.LocalDate
import java.util.*
import javax.crypto.SecretKey
import javax.transaction.Transactional

@Service
class JwtUtil @Autowired constructor(
    private val secretKey: SecretKey,
    private val jwtConfig: JwtConfig,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    @Transactional
    fun createAccessToken(userId: Long): String {
        val user = userRepository.findById(userId).get()
        return createAccessTokenHelper(user)
    }

    @Transactional
    fun createAccessToken(username: String?): String {
        val user = userRepository.findByUsername(username)
        return createAccessTokenHelper(user!!)
    }

    private fun createAccessTokenHelper(user: User): String {
        return jwtConfig.tokenPrefix + createToken(
            getUserClaims(user.id, user.username, user.role, user.activeBlog?.id),
            addHoursToCurrentDate(jwtConfig.accessTokenExpirationInHours)
        )
    }

    @Transactional
    fun createRefreshToken(userId: Long): String {
        val user = userRepository.findById(userId).get()
        return createRefreshTokenHelper(user)
    }

    @Transactional
    fun createRefreshToken(username: String?): String {
        val user = userRepository.findByUsername(username)
        return createRefreshTokenHelper(user!!)
    }

    private fun createToken(claims: Map<String, String>, exp: java.util.Date): String {
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(exp)
            .signWith(secretKey)
            .compact()
    }

    private fun createRefreshTokenHelper(user: User): String {
        val refreshTokenEntity = refreshTokenRepository.save(RefreshToken(user))
        val claims = getUserClaims(user.id, user.username, user.role, user.activeBlog?.id)
        claims["refreshTokenEntityId"] = refreshTokenEntity.id.toString()
        return createToken(
            claims,
            Date.valueOf(LocalDate.now().plusDays(jwtConfig.refreshTokenExpirationInDays.toLong()))
        )
    }

    private fun getUserClaims(
        userId: Long?,
        username: String?,
        userRole: UserRole?,
        activeBlogId: Long?
    ): MutableMap<String, String> {
        val claims: MutableMap<String, String> = HashMap()
        claims["userId"] = userId.toString()
        claims["username"] = username.toString()
        claims["userRole"] = userRole.toString()
        claims["activeBlogId"] = activeBlogId.toString()
        return claims
    }

    private fun addHoursToCurrentDate(hours: Int): java.util.Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.SECOND, hours)
        return calendar.time
    }

    fun extractAllClaims(token: String?): Claims {
        return try {
            Jwts
                .parserBuilder().setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token).body
        } catch (expiredJwtException: ExpiredJwtException) {
            throw AuthenticationException("Token is expired", expiredJwtException)
        } catch (unsupportedJwtException: UnsupportedJwtException) {
            throw AuthenticationException("This JWT token is unsupported", unsupportedJwtException)
        } catch (malformedJwtException: MalformedJwtException) {
            throw AuthenticationException("Malformed JWT", malformedJwtException)
        } catch (signatureException: SignatureException) {
            throw AuthenticationException("JWT Signature invalid", signatureException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            throw AuthenticationException("Illegal argument", illegalArgumentException)
        }
    }

    fun isTokenExpired(claims: Claims): Boolean {
        return claims.expiration.before(Date())
    }

    fun getUserId(claims: Claims): Long {
        return claims["userId"].toString().toLong()
    }

    fun getUserRole(claims: Claims): UserRole {
        return UserRole.valueOf(claims["userRole"].toString())
    }

    fun getUserName(claims: Claims): String {
        return claims["username"].toString()
    }
}