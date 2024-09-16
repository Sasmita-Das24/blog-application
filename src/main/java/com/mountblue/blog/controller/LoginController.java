package com.mountblue.blog.controller;

import com.mountblue.blog.entity.User;
import com.mountblue.blog.service.UserService;
import com.mountblue.blog.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String signUp() {
        return "register";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("register")
    public String registerUser(User user, Model model){
        boolean registered = userService.register(user);
        if (registered) {
            model.addAttribute("message", "User registered Successfully");
            return "login";
        } else {
            model.addAttribute("error", "User Exists Already");
            return "register";
        }
    }
}
