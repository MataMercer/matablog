package com.matamercer.microblog.models.entities.activitypub;

import com.matamercer.microblog.models.entities.BaseModel;
import com.matamercer.microblog.models.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_key_pairs")
@Getter
@Setter
@NoArgsConstructor
public class UserKeyPair extends BaseModel {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 2048)
    private String publicKey;

    @Column(nullable = false, length = 2048)
    private String privateKey;


    public UserKeyPair(User user, String publicKey, String privateKey) {
        this.user = user;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
