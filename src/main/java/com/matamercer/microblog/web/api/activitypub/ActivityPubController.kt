package com.matamercer.microblog.web.api.activitypub

import com.matamercer.microblog.exceptions.NotFoundException
import com.matamercer.microblog.models.repositories.UserKeyPairRepository
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.utilities.EnvironmentUtil
import com.matamercer.microblog.web.api.activitypub.render.KeyRender
import com.matamercer.microblog.web.api.activitypub.render.PersonRender
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.UnknownHostException

@RestController("/activitypub")
class ActivityPubController(
    private val userRepository: UserRepository? = null,
    private val userKeyPairRepository: UserKeyPairRepository? = null,
    private val environmentUtil: EnvironmentUtil? = null
) {

    @GetMapping("/users/{userId}")
    @Throws(UnknownHostException::class)
    fun getPerson(@PathVariable("userId") userId: Long): PersonRender {
        val user = userRepository?.findById(userId)?.orElseThrow {
            throw NotFoundException()
        }
        val keyRender = KeyRender(
            id = environmentUtil?.serverUrl + "/activitypub/users/" + user!!.id + "/main-key",
            owner = environmentUtil?.serverUrl + "/activitypub/users/" + user.id,
            publicKeyPem = userKeyPairRepository!!.findByUser(user)!!.publicKey
        )
        return PersonRender(
            id = environmentUtil!!.serverUrl + "/activitypub/users/" + user.id,
            inbox = environmentUtil.serverUrl + "/activitypub/users/" + user.id + "/inbox",
            name = user.username,
            preferredUsername = user.username,
            outbox = environmentUtil.serverUrl + "/activitypub/users/" + user.id + "/outbox",
            publicKey = keyRender
        )
    }
}