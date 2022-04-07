package com.matamercer.microblog.web.api.activitypub.render

import com.fasterxml.jackson.annotation.JsonProperty

abstract class ActivityRender {
    @JsonProperty("@context")
    val context = listOf("https://www.w3.org/ns/activitystreams", "https://w3id.org/security/v1")
}