package com.netbanking.netbanking_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Welcome to NetBanking App!";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Will render login.html from templates folder
    }

    @GetMapping("/logout-success")
    public String logoutPage() {
        return "logout-success"; // returns logout-success.html
    }
}
