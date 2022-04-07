package com.matamercer.microblog.models.entities

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @ManyToOne
    @JoinColumn(name = "fk_user")
    val user: User? = null
): BaseModel() {}