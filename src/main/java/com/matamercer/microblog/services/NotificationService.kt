package com.matamercer.microblog.services

import com.matamercer.microblog.models.entities.Notification
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList

@Service
class NotificationService(val sseEmitters: CopyOnWriteArrayList<SseEmitter> = CopyOnWriteArrayList<SseEmitter>()) {
    var userId: Long = -1
    fun add(sseEmitter: SseEmitter, userId: Long): SseEmitter {
        this.userId = userId
        sseEmitters.add(sseEmitter)
        sseEmitter.onCompletion { this.sseEmitters.remove(sseEmitter) }
        sseEmitter.onTimeout { sseEmitter.complete(); this.sseEmitters.remove(sseEmitter) }
        return sseEmitter
    }

    fun send(notification: Notification) {
        val failedEmitters = mutableSetOf<SseEmitter>()
        this.sseEmitters.forEach {
            try {
                if (notification.userId == userId) it.send(notification)
            } catch (e: Exception) {
                it.completeWithError(e)
                failedEmitters.add(it)
            }
        }
        this.sseEmitters.removeAll(failedEmitters)
    }
}