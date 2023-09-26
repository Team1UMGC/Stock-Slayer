package com.groupone.controller;

import com.groupone.model.User;
import com.groupone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login/authenticate") // TODO, change in HTML param from username to email
    public String authenticate(@RequestParam("email") String email,
                               @RequestParam("password") String password){
        String direct = "login";
        if(userService.authenticate(email, password)){
            direct = "redirect:/main";
        }
        return direct;
    }
}
