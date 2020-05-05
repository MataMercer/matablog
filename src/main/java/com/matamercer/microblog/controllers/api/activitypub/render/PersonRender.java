package com.matamercer.microblog.controllers.api.activitypub.render;

import com.matamercer.microblog.controllers.api.activitypub.render.ActivityRender;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.utilities.EnvironmentUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.UnknownHostException;

@Getter
@Setter
public class PersonRender extends ActivityRender {
    private String id;
    private final String type = "Person";
    private String preferredUsername;
    private String name;
    private String inbox;
    private String outbox;
    private String liked;
    private KeyRender publicKey;
}
