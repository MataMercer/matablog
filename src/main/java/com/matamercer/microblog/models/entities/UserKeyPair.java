package com.matamercer.microblog.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "UserKeyPairs")
@Getter
@Setter
public class UserKeyPair extends BaseModel{
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 2048)
    private String publicKey;

    @Column(nullable = false, length = 2048)
    private String privateKey;

    public UserKeyPair(){

    }

    public UserKeyPair(User user, String publicKey, String privateKey) {
        this.user = user;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
