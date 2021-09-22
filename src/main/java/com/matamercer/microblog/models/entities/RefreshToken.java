package com.matamercer.microblog.models.entities;

import com.matamercer.microblog.models.entities.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name= "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "fk_user")
    private User user;

}
