package com.example.microblog.security;

import com.example.microblog.models.User;

import java.util.Optional;

public interface ApplicationUserDaoBAK {

    public Optional<User> selectApplicationUserByUsername(String username);
}
