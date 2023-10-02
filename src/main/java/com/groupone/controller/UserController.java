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


    /**
     * Mapping for the index page
     * @return String, name of page that is going to be accessed, returns "index"
     */
    @GetMapping("/")
    public String index(){
        return "index";
    }

    /**
     * Mapping for the login page
     * @return String, name of page that is going to be accessed, returns "login"
     */
    @GetMapping("/login") // TODO, try to figure out a way to save what page the user was trying to access before?
    public String login(){
        return "login";
    }

    /**
     * Post mapping to the backend to validate the login information that was posted
     * @param email String, email of the user that is going to be validated
     * @param password String, password of the user that is going to be validated
     * @return RedirectView, redirects the view to either back to log in if validation failed,
     *         or to main if the validation was successful
     */
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
