package com.matamercer.microblog.configuration

import com.matamercer.microblog.storage.StorageService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*

@Configuration
class WebConfig : WebMvcConfigurer {
    @Bean
    fun init(@Qualifier("fileSystemStorage") storageService: StorageService): CommandLineRunner {
        return CommandLineRunner {
            storageService.init()
        }
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeChangeInterceptor())
    }

    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        val lci = LocaleChangeInterceptor()
        lci.paramName = "lang"
        return lci
    }

    @Bean
    fun localeResolver(): LocaleResolver {
        val slr = SessionLocaleResolver()
        slr.setDefaultLocale(Locale.US)
        return slr
    }

    @Bean
    fun messageSource(): ResourceBundleMessageSource {
        val source = ResourceBundleMessageSource()
        source.setBasenames("messages")
        source.setUseCodeAsDefaultMessage(true)
        return source
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowCredentials = true
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        configuration.allowedHeaders = listOf(
            "X-Requested-With",
            "Origin",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Content-Type",
            "Set-Cookie",
            "Accept",
            "Authorization",
            "authorization"
        )

        configuration.exposedHeaders = listOf(
            "Access-Control-Allow-Headers",
            "Authorization",
            "authorization",
            "refreshToken",
            "x-xsrf-token",
            "Access-Control-Allow-Headers",
            "Origin",
            "Accept",
            "Content-Type",
            "Set-Cookie",
            "Access-Control-Request-Method",
            "Access-Control-Allow-Origin",
            "Access-Control-Request-Headers"
        )
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}