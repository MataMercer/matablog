package com.matamercer.microblog.web.api.activitypub.render

class PersonRender : ActivityRender() {
    var id: String? = null
    var type = "Person"
    var preferredUsername: String? = null
    var name: String? = null
    var inbox: String? = null
    var outbox: String? = null
    var liked: String? = null
    var publicKey: KeyRender? = null
}