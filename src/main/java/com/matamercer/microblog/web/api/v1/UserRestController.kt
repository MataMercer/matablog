package com.matamercer.microblog.web.api.v1

import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.security.CurrentUser
import com.matamercer.microblog.services.UserService
import com.matamercer.microblog.web.api.v1.dto.responses.UserResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserRestController @Autowired constructor(private val userService: UserService) {
    @GetMapping("/currentuser")
    fun currentUser(@CurrentUser userPrincipal: User): ResponseEntity<UserResponseDto?> {
        val user = userService.getUser(userPrincipal.id!!)
        return ResponseEntity.ok(user)
    }
}