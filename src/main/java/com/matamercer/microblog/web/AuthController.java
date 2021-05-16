package com.matamercer.microblog.web;

import com.matamercer.microblog.forms.LoginUserForm;
import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.utilities.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request,
                                          @CookieValue("jid") String refreshToken) throws Exception {
        String accessToken = userService.grantAccessToken(refreshToken);
        return ResponseEntity.ok(new AuthenticationResponse(accessToken));
    }
}
