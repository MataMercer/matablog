package com.example.microblog.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Authorities")
@Getter
@Setter
public class Authority extends BaseModel implements GrantedAuthority {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Type(type = "text")
    private String authorityString;

    @Override
    public String getAuthority() {
        return authorityString;
    }

    public Authority(){}

    public Authority(String authorityString, User user){
        this.authorityString = authorityString;
        this.user = user;
    }
}