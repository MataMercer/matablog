package com.matamercer.microblog.web.api.activitypub;

import com.matamercer.microblog.web.api.activitypub.render.KeyRender;
import com.matamercer.microblog.web.api.activitypub.render.PersonRender;
import com.matamercer.microblog.Exceptions.NotFoundException;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.activitypub.UserKeyPairRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.utilities.EnvironmentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.Optional;


@RestController
@RequestMapping("/activitypub")
class ActivityPubController {

//    @RequestMapping("/secured")
//    public String secured() {
//        System.out.println("Inside secured()");
//        return "Hello user !!! : ";
//    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserKeyPairRepository userKeyPairRepository;

    @Autowired
    private EnvironmentUtil environmentUtil;

    @GetMapping("/users/{userId}")
    public PersonRender getPerson(@PathVariable("userId") Long userId) throws UnknownHostException {
        Optional<User> userOptional = userRepository.findById(userId);
        PersonRender personRender;
        if(userOptional.isPresent()){
            User user = userOptional.get();
            personRender = new PersonRender();
            personRender.setId(environmentUtil.getServerUrl() + "/activitypub/users/" + user.getId());
            personRender.setInbox(personRender.getId() + "/inbox");
            personRender.setName(user.getUsername());
            personRender.setOutbox(personRender.getId() + "/outbox");
            personRender.setPreferredUsername(user.getUsername());

            KeyRender keyRender = new KeyRender();
            keyRender.setOwner(environmentUtil.getServerUrl() + "/activitypub/users/" + user.getId());
            keyRender.setId(keyRender.getOwner() + "/main-key");
            keyRender.setPublicKeyPem(userKeyPairRepository.findByUser(user).getPublicKey());
            personRender.setPublicKey(keyRender);
        }else{
            throw new NotFoundException();
        }

        return personRender;
    }



}