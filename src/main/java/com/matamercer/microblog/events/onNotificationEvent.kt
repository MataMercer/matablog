package com.matamercer.microblog.events

import com.matamercer.microblog.models.entities.Notification
import org.springframework.context.ApplicationEvent

class onNotificationEvent(val notification: Notification): ApplicationEvent(Notification())