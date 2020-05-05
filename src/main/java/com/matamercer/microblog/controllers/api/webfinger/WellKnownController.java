package com.matamercer.microblog.controllers.api.webfinger;

import com.matamercer.microblog.errors.NotFoundException;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.utilities.EnvironmentUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.*;

@RestController
@RequestMapping("/.well-known")
public class WellKnownController {

    @Autowired
    EnvironmentUtil environmentUtil;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/webfinger")
    @ResponseBody
    public JSONResourceDescriptor getItem(@RequestParam("resource") Optional<String> resource) {
        JSONResourceDescriptor jsonResourceDescriptor = new JSONResourceDescriptor();
        if(resource.isPresent()){
            String[] splitResource = (resource.get()).split("[:@]",3);
            String username = splitResource[1];


            User user = userRepository.findByUsername(username);
            if(user == null){
                throw new NotFoundException();
            }

            jsonResourceDescriptor.setSubject(resource.get());
            try {
                jsonResourceDescriptor.setLinks(Arrays.asList(new Link("self", "application/activity+json", environmentUtil.getServerUrl() + "/activitypub/users/" + user.getId() )));
                return jsonResourceDescriptor;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }else{
            throw new NotFoundException();
        }

        return jsonResourceDescriptor;
    }

    @Getter
    @Setter
    private class JSONResourceDescriptor{
        private String subject;
        private List<Link> links;
        private List<String> aliases;
        private Map<String, String> properties;
    }

    @Getter
    @Setter
    private class Link{
        private String rel;
        private String type;
        private String href;
        private String titles;
        private String properties;

        public Link(String rel, String type, String href) {
            this.rel = rel;
            this.type = type;
            this.href = href;
        }
    }

}
