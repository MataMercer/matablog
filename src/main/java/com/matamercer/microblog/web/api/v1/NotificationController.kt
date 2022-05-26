package com.matamercer.microblog.web.api.v1

import com.matamercer.microblog.events.onNotificationEvent
import com.matamercer.microblog.security.CurrentUser
import com.matamercer.microblog.security.UserPrincipal
import com.matamercer.microblog.services.NotificationService
import org.springframework.context.ApplicationListener
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1/notification")
class NotificationController(val notificationService: NotificationService):ApplicationListener<onNotificationEvent> {

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getNotification(@CurrentUser userPrincipal: UserPrincipal): SseEmitter {
        return notificationService.add(SseEmitter(),userPrincipal.id)
    }

     override fun onApplicationEvent(onNotificationEvent: onNotificationEvent) {
        notificationService.send(onNotificationEvent.notification)
    }
}

