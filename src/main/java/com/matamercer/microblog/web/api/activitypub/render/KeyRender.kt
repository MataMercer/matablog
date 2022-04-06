package com.matamercer.microblog.web.api.activitypub.render

import lombok.Getter
import lombok.Setter

@Getter
@Setter
class KeyRender {
    var id: String? = null
    var owner: String? = null
    var publicKeyPem: String? = null
}