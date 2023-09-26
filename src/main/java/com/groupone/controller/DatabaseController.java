package com.groupone.controller;

import com.groupone.api.DatabaseAPI;
import com.groupone.model.Stock;
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

    private final RedirectView defaultRedirection = new RedirectView("/database");

    @RequestMapping("/database")
    public String databaseIndex(Model model){
        model.addAttribute("userList", databaseService.getUserTableInfo());
        model.addAttribute("stockList", databaseService.getStockTableInfo());
        return "databaseIndex";
    }

    @RequestMapping("/database/delete/user/{userId}")
    public RedirectView deleteUser(@PathVariable int userId){
        databaseService.deleteUserRecord(userId);
        return defaultRedirection;
    }

    @RequestMapping("/database/delete/stock/{stockId}")
    public RedirectView deleteStock(@PathVariable int stockId){
        databaseService.deleteStockRecord(stockId);
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

    @PostMapping("/database/add/user")
    public RedirectView addUser(@ModelAttribute User user){
        databaseService.addUserRecord(user.getEmail(), user.getPassword());
        return defaultRedirection;
    }

    @PostMapping("database/add/stock")
    public RedirectView addStock(@RequestParam int ownerId, @RequestParam String symbol,
                                 @RequestParam Double volume, @RequestParam Double value){
        databaseService.addStockRecord(ownerId, symbol, volume, value);
        return defaultRedirection;
    }
}
