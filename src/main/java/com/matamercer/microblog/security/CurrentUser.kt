package com.matamercer.microblog.security

import org.springframework.security.core.annotation.AuthenticationPrincipal
import kotlin.annotation.Retention

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AuthenticationPrincipal
annotation class CurrentUser 