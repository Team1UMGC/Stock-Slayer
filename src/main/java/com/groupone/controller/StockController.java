package com.groupone.controller;

import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.groupone.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StockController {
    @Autowired
    StockService stockService;

    @GetMapping("/main")
    public String mainPage(Model model){
        model.addAttribute("heldStocks", stockService.getHeldStocks());
        model.addAttribute("stockPrices", stockService.getStockPrices());
        model.addAttribute("userFunds", stockService.getUserFunds());

        return "main";
    }

    @PostMapping("/search") // TODO, rename HTML param to symbol rather than stockName
    public String searchStock(@RequestParam("symbol") String symbol, Model model){
        StockUnit stockUnit = null;
        double stockPrice = 0.0;
        try{
            stockUnit = stockService.requestStock(symbol);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

        if(stockUnit != null){
            stockPrice = stockUnit.getClose();
            model.addAttribute("symbol", symbol);
            model.addAttribute("stockPrice", stockPrice);
        } else {
            model.addAttribute("symbol", symbol);
            model.addAttribute("stockPrice", "N/A");
        }

        model.addAttribute("heldStocks", stockService.getHeldStocks());
        model.addAttribute("stockPrices", stockService.getStockPrices());
        model.addAttribute("userFunds", stockService.getUserFunds());

        return "main";
    }

    @PostMapping("/buy")
    public String buyStock(@RequestParam("symbol") String symbol,
                           @RequestParam("price") double price,
                           @RequestParam("buyShares") int buyShares,
                           Model model){
        // TODO, write method body
        return null;
    }

    @PostMapping("/sell/{index}")
    public String sellStock(@PathVariable("index") int index, Model model){
        if(stockService.getHeldStocks().size() > index && index >= 0){
            stockService.sellStock(index);
        }

        model.addAttribute("heldStocks", stockService.getHeldStocks());
        model.addAttribute("stockPrices", stockService.getStockPrices());
        model.addAttribute("userFunds", stockService.getUserFunds());

        return "main";
    }

    @PostMapping("/sortShares")
    public String sortShares(){
        stockService.sort();
        return "redirect:/main";
    }

    @PostMapping("/sortHighestValue")
    public String sortHighestValue() {
        stockService.sortByHighest();
        return "redirect:/main";
    }

    @PostMapping("/sortLowestValue")
    public String sortLowestValue(){
        stockService.sortByLowest();
        return "redirect:/main";
    }
}
