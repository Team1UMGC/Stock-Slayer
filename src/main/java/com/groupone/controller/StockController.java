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

    /**
     * Main page that will be used for buying and selling stocks
     * @param model Model, DOM that will have attributes added to it
     * @return String, redirects either back to login page if the current logged user is null,
     *         or back to main to load the information
     */
    @GetMapping("/main")
    public String mainPage(Model model){
        User user = userService.getLogged();
        if(user == null) return "login";

        try{
            model.addAttribute("heldStocks", stockService.getHeldStocks(user));
            model.addAttribute("stockPrices", stockService.getStockPrices(user));
            model.addAttribute("userFunds", stockService.getUserFunds(user));
        }catch(Exception e){
            System.err.println(e.getMessage());
        }

        return "main";
    }

    /**
     * Post mapping for the search function of the main page.
     * Searches for an inputted stock symbol and returns the current price.
     * @param symbol String, symbol that the API will search for
     * @param model Model, DOM that will have attributes added to it
     * @return String, redirects either back to login page if the current logged user is null,
     *         or back to main to load the information
     */
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

        try{
            model.addAttribute("heldStocks", stockService.getHeldStocks(user));
            model.addAttribute("stockPrices", stockService.getStockPrices(user));
            model.addAttribute("userFunds", stockService.getUserFunds(user));
        }catch(Exception e){
            System.err.println(e.getMessage());
        }

        return "main";
    }

    /**
     * Post Mapping to buy selected stocks
     * @param symbol String, symbol of the stock that is being purchased
     * @param priceStr String, string of the price of the stock per share
     * @param buySharesStr String, string of the number of shares to purchase
     * @param model Model, DOM that will have attributes added to it
     * @return String, redirects either back to login page if the current logged user is null,
     *         or back to main to load the information
     */
    @PostMapping("/buy")
    public String buyStock(@RequestParam("symbol") String symbol,
                           @RequestParam("price") String priceStr,
                           @RequestParam("buyShares") String buySharesStr,
                           Model model){
        User user = userService.getLogged();
        if(user == null) return "login";

        try{
            stockService.purchaseStock(user, new Stock(symbol,
                Double.parseDouble(priceStr),
                Double.parseDouble(buySharesStr)
            ));
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

        return "main";
    }

    /**
     * Post Mapping for selling an owned stock
     * @param index int, Index of what stock to sell
     * @param model Model, DOM that will have attributes added to it
     * @return String, redirects either back to login page if the current logged user is null,
     *         or back to main to load the information
     */
    @PostMapping("/sell/{index}")
    public String sellStock(@PathVariable("index") int index, Model model){
        User user = userService.getLogged();
        if(user == null) return "login";

        try{
            if(stockService.getHeldStocks(user).size() > index && index >= 0){
                stockService.sellStock(user, index);
            }

            model.addAttribute("heldStocks", stockService.getHeldStocks(user));
            model.addAttribute("stockPrices", stockService.getStockPrices(user));
            model.addAttribute("userFunds", stockService.getUserFunds(user));
        }catch(Exception e){
            System.err.println(e.getMessage());
        }

        return "main";
    }

    /**
     *
     * @return String, redirects either back to login page if the current logged user is null,
     *         or back to main to load the information
     */
    @PostMapping("/sortShares")
    public String sortShares(){
        User user = userService.getLogged();
        if(user == null) return "login";
        stockService.sort();
        return "redirect:/main";
    }

    /**
     *
     * @return String, redirects either back to login page if the current logged user is null,
     *         or back to main to load the information
     */
    @PostMapping("/sortHighestValue")
    public String sortHighestValue() {
        User user = userService.getLogged();
        if(user == null) return "login";
        stockService.sortByHighest();
        return "redirect:/main";
    }

    /**
     *
     * @return String, redirects either back to login page if the current logged user is null,
     *         or back to main to load the information
     */
    @PostMapping("/sortLowestValue")
    public String sortLowestValue(){
        User user = userService.getLogged();
        if(user == null) return "login";
        stockService.sortByLowest();
        return "redirect:/main";
    }
}
