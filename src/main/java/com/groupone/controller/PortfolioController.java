package com.groupone.controller;

import com.groupone.model.User;
import com.groupone.service.StockService;
import com.groupone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PortfolioController {
    @Autowired
    StockService stockService;

    @Autowired
    UserService userService;

    RedirectView defaultRedirect = new RedirectView("/portfolio");

    /**
     * Get Mapping for the portfolio page
     * @param model Model, DOM that will have attributes added to it
     * @return String, redirects either back to login page if the current logged user is null,
     *         or back to portfolio to load the information
     */
    @GetMapping("/portfolio")
    public String portfolio(Model model){
        User user = userService.getLogged();
        if(user == null) return "/login";

        System.out.println(stockService.getHeldStocks(user));

        model.addAttribute("heldStocksList", stockService.getHeldStocks(user));
        model.addAttribute("userFunds", stockService.getUserFunds(user));

        return "/portfolio";
    }

    @RequestMapping("/portfolio/sell/{stockId}")
    public RedirectView portfolioSellStock(@PathVariable int stockId){
        stockService.sellStock(userService.getLogged(), stockId);
        return defaultRedirect;
    }
}
