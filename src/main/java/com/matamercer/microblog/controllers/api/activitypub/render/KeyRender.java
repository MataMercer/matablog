package com.matamercer.microblog.controllers.api.activitypub.render;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyRender {
    private String id;
    private String owner;
    private String publicKeyPem;
}
