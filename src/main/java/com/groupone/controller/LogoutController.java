package com.groupone.controller;

import com.groupone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LogoutController {

    @Autowired
    UserService userService;
    
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        userService.logout();
        return "redirect:/login";
    }
}
