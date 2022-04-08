package com.matamercer.microblog.web.api.v1

import com.matamercer.microblog.services.UserService
import com.matamercer.microblog.utilities.AuthenticationResponse
import com.matamercer.microblog.web.api.v1.dto.requests.RefreshTokenRequestDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/auth")
class AuthRestController @Autowired constructor(private val userService: UserService) {
    @PostMapping("/refreshtoken")
    fun refreshToken(@RequestBody refreshTokenRequestDTO: @Valid RefreshTokenRequestDto): ResponseEntity<*> {
        val accessToken = userService.grantAccessToken(refreshTokenRequestDTO.refreshToken)
        return ResponseEntity.ok(AuthenticationResponse(accessToken!!))
    }
}