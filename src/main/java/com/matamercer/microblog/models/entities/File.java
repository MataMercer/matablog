package com.matamercer.microblog.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class File extends BaseModel{

    @Column(nullable = false)
    public String name;

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog owner;


}
