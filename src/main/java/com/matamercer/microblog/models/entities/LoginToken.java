package com.matamercer.microblog.models.entities;

import com.matamercer.microblog.models.entities.BaseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name= "login_tokens")
@Getter
@Setter
@NoArgsConstructor
public class LoginToken extends BaseModel {

    @Column(nullable = false)
    private String series;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Date lastUsed;

    public LoginToken(String series, User user, String token, Date lastUsed) {
        this.series = series;
        this.user = user;
        this.token = token;
        this.lastUsed = lastUsed;
    }

}
