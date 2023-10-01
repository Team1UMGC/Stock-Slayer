package com.groupone.controller;

import com.groupone.model.User;
import com.groupone.service.StockService;
import com.groupone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortfolioController {
    @Autowired
    StockService stockService;

    @Autowired
    UserService userService;

    @GetMapping("/portfolio")
    public String portfolio(Model model){
        User user = userService.getLogged();
        if(user == null) return "login";

        System.out.println(stockService.getHeldStocks(user));

        model.addAttribute("heldStocksList", stockService.getHeldStocks(user));
        model.addAttribute("userFunds", stockService.getUserFunds(user));

        return "portfolio";
    }
}
