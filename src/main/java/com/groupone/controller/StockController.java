package com.groupone.controller;

import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.groupone.model.Stock;
import com.groupone.model.User;
import com.groupone.service.StockService;
import com.groupone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class StockController {
    @Autowired
    StockService stockService;

    @Autowired
    UserService userService;

    @GetMapping("/main")
    public String mainPage(Model model){
        User user = userService.getLogged();
        if(user == null) return "login";

        model.addAttribute("heldStocks", stockService.getHeldStocks(user));
        model.addAttribute("stockPrices", stockService.getStockPrices(user));
        model.addAttribute("userFunds", stockService.getUserFunds(user));

        return "main";
    }

    @PostMapping("/search")
    public String searchStock(@RequestParam("symbol") String symbol, Model model){
        User user = userService.getLogged();
        if(user == null) return "login";

        double stockPrice = 0.0;
        try{
            stockPrice = stockService.requestStockPrice(symbol);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

        if(stockPrice != 0.0){
            model.addAttribute("symbol", symbol);
            model.addAttribute("stockPrice", stockPrice);
        } else {
            model.addAttribute("symbol", symbol);
            model.addAttribute("stockPrice", "N/A");
        }

        model.addAttribute("heldStocks", stockService.getHeldStocks(user));
        model.addAttribute("stockPrices", stockService.getStockPrices(user));
        model.addAttribute("userFunds", stockService.getUserFunds(user));

        return "main";
    }

    @PostMapping("/buy")
    public String buyStock(@RequestParam("symbol") String symbol,
                           @RequestParam("price") String priceStr,
                           @RequestParam("buyShares") String buySharesStr,
                           Model model){
        User user = userService.getLogged();
        if(user == null) return "login";
        double price = Double.parseDouble(priceStr);
        double buyShares = Double.parseDouble(buySharesStr);

        double totalCost = price * buyShares;
        if(user.getAvailableFunds() >= totalCost && buyShares > 0){
            user.subtractFunds(totalCost);
            user.addStock(symbol, buyShares, price);
            stockService.addStockToDatabase(user, new Stock(symbol, buyShares, price));
        }

        return "main";
    }

    @PostMapping("/sell/{index}")
    public String sellStock(@PathVariable("index") int index, Model model){
        User user = userService.getLogged();
        if(user == null) return "login";
        if(stockService.getHeldStocks(user).size() > index && index >= 0){
            stockService.sellStock(index);
        }

        model.addAttribute("heldStocks", stockService.getHeldStocks(user));
        model.addAttribute("stockPrices", stockService.getStockPrices(user));
        model.addAttribute("userFunds", stockService.getUserFunds(user));

        return "main";
    }

    @PostMapping("/sortShares")
    public String sortShares(){
        User user = userService.getLogged();
        if(user == null) return "login";
        stockService.sort();
        return "redirect:/main";
    }

    @PostMapping("/sortHighestValue")
    public String sortHighestValue() {
        User user = userService.getLogged();
        if(user == null) return "login";
        stockService.sortByHighest();
        return "redirect:/main";
    }

    @PostMapping("/sortLowestValue")
    public String sortLowestValue(){
        User user = userService.getLogged();
        if(user == null) return "login";
        stockService.sortByLowest();
        return "redirect:/main";
    }
}
