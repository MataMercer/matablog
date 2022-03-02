package com.matamercer.microblog.models.entities

import javax.persistence.*

@Entity
@Table(name = "user_key_pairs")
class UserKeyPair(
    @field:JoinColumn(name = "userKeyPairs") @field:ManyToOne private val user: User, @field:Column(
        nullable = false,
        length = 2048
    ) val publicKey: String, @field:Column(nullable = false, length = 2048) private val privateKey: String
) : BaseModel()