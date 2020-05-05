package com.matamercer.microblog.models.entities;

import com.matamercer.microblog.models.entities.BaseModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name= "Login_tokens")
@Getter
@Setter
public class LoginToken extends BaseModel {

    @Column(nullable = false)
    private String series;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Date lastUsed;

    public LoginToken(){}

    public LoginToken(String series, String username, String token, Date lastUsed) {
        this.series = series;
        this.username = username;
        this.token = token;
        this.lastUsed = lastUsed;
    }

}
