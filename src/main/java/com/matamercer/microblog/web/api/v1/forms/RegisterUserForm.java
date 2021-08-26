package com.matamercer.microblog.web.api.v1.forms;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class RegisterUserForm {
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
