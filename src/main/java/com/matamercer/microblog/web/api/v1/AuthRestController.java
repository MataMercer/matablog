package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.utilities.AuthenticationResponse;
import com.matamercer.microblog.web.api.v1.dto.requests.RefreshTokenRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {
    private final UserService userService;

    @Autowired
    public AuthRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDTO) {
        String accessToken = userService.grantAccessToken(refreshTokenRequestDTO.getRefreshToken());
        return ResponseEntity.ok(new AuthenticationResponse(accessToken));
    }
}
