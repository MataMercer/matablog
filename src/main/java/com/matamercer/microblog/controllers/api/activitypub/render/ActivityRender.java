package com.matamercer.microblog.controllers.api.activitypub.render;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public abstract class ActivityRender {

    @JsonProperty("@context")
    private final List<String> context= Arrays.asList("https://www.w3.org/ns/activitystreams", "https://w3id.org/security/v1");

}
