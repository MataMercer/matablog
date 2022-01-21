package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.security.CurrentUser;
import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.web.api.v1.dto.responses.UserResponseDto;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/currentuser")
    public ResponseEntity<UserResponseDto> currentUser(@CurrentUser User userPrincipal) {
       val user = userService.getUser(userPrincipal.getId());
        return ResponseEntity.ok(user);
    }
}
