package com.matamercer.microblog.services

import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.entities.UserKeyPair
import com.matamercer.microblog.models.repositories.UserKeyPairRepository
import org.springframework.stereotype.Service
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.util.*

@Service
class UserKeyPairService(private val userKeyPairRepository: UserKeyPairRepository) {
    @Throws(NoSuchAlgorithmException::class)
    fun createUserKeyPairForUser(user: User?): UserKeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()
        val userKeyPair = UserKeyPair(
            user!!,
            Base64.getMimeEncoder().encodeToString(keyPair.public.encoded),
            Base64.getMimeEncoder().encodeToString(keyPair.private.encoded)
        )
        return userKeyPairRepository.save(userKeyPair)
    }
}