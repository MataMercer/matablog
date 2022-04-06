package com.matamercer.microblog.web.api.activitypub.render

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

abstract class ActivityRender {
    @JsonProperty("@context")
    private val context = Arrays.asList("https://www.w3.org/ns/activitystreams", "https://w3id.org/security/v1")
}