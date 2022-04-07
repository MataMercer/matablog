package com.matamercer.microblog.web.api.activitypub.render


class PersonRender(
    var id: String? = null,
    val type: String? = "Person",
    val preferredUsername: String? = null,
    val name: String? = null,
    var inbox: String? = null,
    val outbox: String? = null,
    val liked: String? = null,
    val publicKey: KeyRender? = null
) : ActivityRender()