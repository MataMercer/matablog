package com.matamercer.microblog.security

import com.matamercer.microblog.models.entities.User
import org.springframework.context.ApplicationEvent
import java.util.*

class OnRegistrationCompleteEvent(
    var user: User,
    var locale: Locale,
    var appUrl: String
) : ApplicationEvent(user)