package com.matamercer.microblog.web.api.activitypub

import com.matamercer.microblog.exceptions.NotFoundException
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.UserKeyPairRepository
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.utilities.EnvironmentUtil
import com.matamercer.microblog.web.api.activitypub.render.KeyRender
import com.matamercer.microblog.web.api.activitypub.render.PersonRender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.UnknownHostException
import java.util.*

@RestController
@RequestMapping("/activitypub")
internal class ActivityPubController {
    //    @RequestMapping("/secured")
    //    public String secured() {
    //        System.out.println("Inside secured()");
    //        return "Hello user !!! : ";
    //    }
    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val userKeyPairRepository: UserKeyPairRepository? = null

    @Autowired
    private val environmentUtil: EnvironmentUtil? = null
    @GetMapping("/users/{userId}")
    @Throws(UnknownHostException::class)
    fun getPerson(@PathVariable("userId") userId: Long?): PersonRender {
        val userOptional = userRepository?.findById(userId) ?: throw NotFoundException()

        val user = userOptional.get()
        val personRender = PersonRender()
        personRender.id = environmentUtil!!.serverUrl + "/activitypub/users/" + user.id
        personRender.inbox = personRender.id + "/inbox"
        personRender.name = user.username
        personRender.outbox = personRender.id + "/outbox"
        personRender.preferredUsername = user.username
        val keyRender = KeyRender()
        keyRender.owner = environmentUtil!!.serverUrl + "/activitypub/users/" + user.id
        keyRender.id = keyRender.owner + "/main-key"
        keyRender.publicKeyPem = userKeyPairRepository!!.findByUser(user)!!.publicKey
        personRender.publicKey = keyRender

        return personRender
    }
}