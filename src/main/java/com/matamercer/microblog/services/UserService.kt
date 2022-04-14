package com.matamercer.microblog.services

import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.entities.VerificationToken
import com.matamercer.microblog.models.repositories.RefreshTokenRepository
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.models.repositories.VerificationTokenRepository
import com.matamercer.microblog.security.authentication.JwtUtil
import com.matamercer.microblog.security.authorization.oauth.GithubOauthConfig
import com.matamercer.microblog.web.api.v1.dto.mappers.toUserResponseDto
import com.matamercer.microblog.web.api.v1.dto.responses.UserResponseDto
import com.matamercer.microblog.web.error.exceptions.RevokedRefreshTokenException
import com.matamercer.microblog.web.error.exceptions.UserAlreadyExistsException
import com.matamercer.microblog.web.error.exceptions.UserNotFoundException
import io.jsonwebtoken.Jwts
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.crypto.SecretKey

@Service
@Slf4j
class UserService(
    private val userRepository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val userKeyPairService: UserKeyPairService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val secretKey: SecretKey,
    private val jwtUtil: JwtUtil,
    private val githubOauthConfig: GithubOauthConfig
) : UserDetailsService {
    @Transactional
    fun createUser(user: User): User {
        if (emailExists(user.email) || usernameExists(user.username)) {
            throw UserAlreadyExistsException("There is already an account with that email.")
        }
        val registeredUser = userRepository.save(user)
        userKeyPairService.createUserKeyPairForUser(user)
        return registeredUser
    }

    @Transactional(readOnly = true)
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found.")
        val builder: org.springframework.security.core.userdetails.User.UserBuilder?
        builder = org.springframework.security.core.userdetails.User.withUsername(username)
        builder.disabled(!user.isEnabled)
        builder.password(user.password)
        builder.authorities(user.authorities)
        return builder.build()
    }

    @Transactional
    fun grantAccessToken(refreshToken: String?): String? {
        val claimsJws = Jwts
            .parserBuilder().setSigningKey(secretKey)
            .build()
            .parseClaimsJws(refreshToken)
        val body = claimsJws!!.body
        val userId = body!!["userId"].toString().toLong()
        val refreshTokenId = body["refreshTokenEntityId"].toString().toLong()
        refreshTokenRepository.findById(refreshTokenId).orElseThrow {
            throw RevokedRefreshTokenException()
        }
        return jwtUtil.createAccessToken(userId)
    }

    fun createVerificationTokenForUser(user: User?, token: String) {
        val myToken = VerificationToken(token, user)
        verificationTokenRepository.save(myToken)
    }

    fun generateNewVerificationToken(existingVerificationToken: String?): VerificationToken? {
        var verificationToken = verificationTokenRepository.findByToken(existingVerificationToken)
        verificationToken!!.updateToken(UUID.randomUUID().toString())
        verificationToken = verificationTokenRepository.save(verificationToken)
        return verificationToken
    }

    fun getVerificationToken(VerificationToken: String?): VerificationToken? {
        return verificationTokenRepository.findByToken(VerificationToken)
    }

    fun getUser(verificationToken: VerificationToken?): User? {
        val token = verificationTokenRepository.findByToken(verificationToken!!.token)
        return token?.user
    }

    fun getUser(id: Long): UserResponseDto? {
        val user = userRepository.findById(id).orElseThrow {
            throw UserNotFoundException("Unable to create post because unable to find logged in user.")

        }
        return user.toUserResponseDto()
    }

    fun save(user: User): User? {
        return userRepository.save(user)
    }

    fun delete(user: User) {
        userRepository.delete(user)
    }

    fun emailExists(email: String?): Boolean {
        return userRepository.findByEmail(email) != null
    }

    fun usernameExists(username: String?): Boolean {
        return userRepository.findByUsername(username) != null
    }
}