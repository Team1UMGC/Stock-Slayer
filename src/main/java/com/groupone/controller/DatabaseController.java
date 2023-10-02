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

    // Default page to redirect to after loading
    private final RedirectView defaultRedirection = new RedirectView("/database");

    /**
     * Request Mapping for the database page
     * @param model Model, DOM that will have attributes added to it
     * @return String, name of the page to load, which is "databaseIndex"
     */
    @RequestMapping("/database")
    public String databaseIndex(Model model){
        model.addAttribute("userList", databaseService.getUserTableInfo());
        model.addAttribute("stockList", databaseService.getStockTableInfo());
        return "databaseIndex";
    }

    /**
     * Request Mapping for deleting a user
     * @param userId int, user ID of the user to delete from the database
     * @return defaultRedirection
     */
    @RequestMapping("/database/delete/user/{userId}")
    public RedirectView deleteUser(@PathVariable int userId){
        databaseService.deleteUserRecord(userId);
        return defaultRedirection;
    }

    /**
     * Request Mapping for deleting a stock
     * @param stockId int, stock ID of the stock to delete from the database
     * @return defaultRedirection
     */
    @RequestMapping("/database/delete/stock/{stockId}")
    public RedirectView deleteStock(@PathVariable int stockId){
        databaseService.deleteStockRecord(stockId);
        return defaultRedirection;
    }

//    @PostMapping("/database/update/") FIXME need to fix the databaseAPI before this can be utilized...
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

    /**
     * Post Mapping for adding a user to the database
     * @param user User, user to add to the database
     * @return defaultRedirection
     */
    @PostMapping("/database/add/user")
    public RedirectView addUser(@ModelAttribute User user){
        databaseService.addUserRecord(user.getEmail(), user.getPassword());
        return defaultRedirection;
    }

    /**
     * Post Mapping for adding a stock to the database
     * @param ownerId int, the ID of the owner of the stock
     * @param symbol String, the symbol of the stock
     * @param volume double, the volume, or number of shares purchased
     * @param value double, the value of each share
     * @return defaultRedirection
     */
    @PostMapping("database/add/stock")
    public RedirectView addStock(@RequestParam int ownerId, @RequestParam String symbol,
                                 @RequestParam double volume, @RequestParam double value){
        databaseService.addStockRecord(ownerId, symbol, volume, value);
        return defaultRedirection;
    }
}
