package com.matamercer.microblog.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RegisterUserForm {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
