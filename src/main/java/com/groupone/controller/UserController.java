package com.groupone.controller;

import com.groupone.model.User;
import com.groupone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService userService;


    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/login") // TODO, try to figure out a way to save what page the user was trying to access before
    public String login(){
        return "login";
    }

    @PostMapping("/login/authenticate")
    public RedirectView authenticate(@RequestParam("email") String email,
                                     @RequestParam("password") String password){
        RedirectView direct = new RedirectView("/login");
        if(userService.authenticate(email, password)){
            userService.setLogged(new User(email, password));
            direct = new RedirectView("/main");
        }
        return direct;
    }
}
