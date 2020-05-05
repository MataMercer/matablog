package com.matamercer.microblog.controllers.api.activitypub.render;

import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserKeyPairRepository;
import com.matamercer.microblog.utilities.EnvironmentUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.UnknownHostException;

@Getter
@Setter
public class KeyRender {
    private String id;
    private String owner;
    private String publicKeyPem;
}
