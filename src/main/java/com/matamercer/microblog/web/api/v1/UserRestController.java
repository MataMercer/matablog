package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.services.UserService;
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
    public ResponseEntity<?> currentUser(Principal principal) {
       val user = userService.getUser(principal);
        return ResponseEntity.ok(user);
    }
}
