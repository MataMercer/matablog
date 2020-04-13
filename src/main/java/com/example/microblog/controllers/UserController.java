package com.example.microblog.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class UserController {

    @GetMapping("login")
    public String getLoginView(){
        return "login";
    }

    @GetMapping("dashboard")
    public String getDashboardView(){
        return "dashboard";
    }

    @GetMapping("admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String getAdminDashboardView(){
        return "adminDashboard";
    }

}
