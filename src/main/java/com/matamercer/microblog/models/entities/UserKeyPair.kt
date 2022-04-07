package com.matamercer.microblog.models.entities

import javax.persistence.*

@Entity
@Table(name = "user_key_pairs")
class UserKeyPair(
    @JoinColumn(name = "userKeyPairs")
    @ManyToOne
    private val user: User? = null,

    @Column(
        nullable = false,
        length = 2048
    )
    val publicKey: String? = null,

    @Column(nullable = false, length = 2048)
    private val privateKey: String? = null
) : BaseModel()