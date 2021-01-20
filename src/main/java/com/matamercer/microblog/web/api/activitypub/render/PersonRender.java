package com.matamercer.microblog.web.api.activitypub.render;

import lombok.Getter;
import lombok.Setter;

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
