package com.matamercer.microblog.web.api.webfinger

import com.matamercer.microblog.exceptions.NotFoundException
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.utilities.EnvironmentUtil
import lombok.Getter
import lombok.Setter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/.well-known")
class WellKnownController {
    @Autowired
    var environmentUtil: EnvironmentUtil? = null

    @Autowired
    var userRepository: UserRepository? = null

    @GetMapping("/webfinger")
    @ResponseBody
    fun getItem(@RequestParam("resource") resource: Optional<String>): JSONResourceDescriptor {
        val jsonResourceDescriptor = JSONResourceDescriptor()
        return if (resource.isPresent) {
            val splitResource = resource.get().split("[:@]".toRegex(), 3).toTypedArray()
            val username = splitResource[1]
            val optionalUser = userRepository!!.findByUsername(username)
            if (!optionalUser.isPresent) {
                throw NotFoundException()
            }
            val user = optionalUser.get()
            jsonResourceDescriptor.subject = resource.get()
            try {
                jsonResourceDescriptor.links = listOf(
                    Link(
                        "self",
                        "application/activity+json",
                        environmentUtil!!.serverUrl + "/activitypub/users/" + user.id
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            jsonResourceDescriptor
        } else {
            throw NotFoundException()
        }
    }

    inner class JSONResourceDescriptor {
        var subject: String? = null
        var links: List<Link>? = null
        var aliases: List<String>? = null
        var properties: Map<String, String>? = null
    }

    inner class Link(private val rel: String, private val type: String, private val href: String) {
        var titles: String? = null
        var properties: String? = null
    }
}