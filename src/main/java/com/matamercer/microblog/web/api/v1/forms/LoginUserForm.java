package com.matamercer.microblog.web.api.v1.forms;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserForm {
    private String username;
    private String password;


}
