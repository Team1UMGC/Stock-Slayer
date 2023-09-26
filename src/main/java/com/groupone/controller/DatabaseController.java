package com.groupone.controller;

import com.groupone.api.DatabaseAPI;
import com.groupone.model.User;
import com.groupone.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DatabaseController {
    @Autowired
    DatabaseService databaseService;

    @Autowired
    DatabaseAPI databaseAPI;

    private final RedirectView defaultRedirection = new RedirectView("/database");

    @RequestMapping("/database")
    public String databaseIndex(Model model){
        model.addAttribute("userList", databaseService.getUserTableInfo());
        model.addAttribute("stockList", databaseService.getStockTableInfo());
        return "databaseIndex";
    }

    @RequestMapping("/database/delete/{useremail}")
    public RedirectView deleteUser(@PathVariable String useremail){
        databaseAPI.deleteUserRecord(useremail); //TODO, if the user has stocks, they should be deleted too
        return defaultRedirection;
    }

    @RequestMapping("/database/delete/{stockId}")
    public RedirectView deleteStock(@PathVariable int stockId){
        databaseAPI.deleteStockRecord(stockId);
        return defaultRedirection;
    }

//    @PostMapping("/database/update/")
//    public RedirectView updateUser(@ModelAttribute User user){
//        System.out.println(user);
//        User foundUser = null;
//        try{
//            foundUser = databaseService.getUser(user.getID());
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//        if(foundUser!=null) databaseAPI.updateUserRecord(foundUser, user);
//        return defaultRedirection;
//    }

    @PostMapping("/database/add/")
    public RedirectView addUser(@ModelAttribute User user){
        databaseAPI.addUserRecord(user.getEmail(), user.getPassword());
        return defaultRedirection;
    }
}
