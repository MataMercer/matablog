package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.web.error.UserNotFoundException;
import lombok.var;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {
    private final UserRepository userRepository;

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/currentuser")
    public ResponseEntity<?> currentUser(HttpServletRequest request) {
        var optionalUser = userRepository.findByUsername(request.getUserPrincipal().getName());
        if(!optionalUser.isPresent()){
            throw new UserNotFoundException("Unable to find current user.");
        }
        User user = optionalUser.get();
        return ResponseEntity.ok(user.getUsername());
    }
}
