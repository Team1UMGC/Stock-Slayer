package com.groupone.service;

import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.groupone.api.DatabaseAPI;
import com.groupone.api.StockAPI;
import com.groupone.model.Stock;
import com.groupone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockService {
    @Autowired
    StockAPI stockAPI;

    @Autowired
    DatabaseAPI databaseAPI;


    public List<Stock> getHeldStocks(User user) {
        User userRecord = databaseAPI.getUserRecord(user);
        return userRecord.getStocks();
    }

    public Map<String, Double> getStockPrices(User user) {
        User userRecord = databaseAPI.getUserRecord(user);
        Map<String, Double> stockPrices = new HashMap<>(); // TODO, abstract this into databaseAPI as a method
        for (Stock recordedStock: userRecord.getStocks()) {
            stockPrices.put(recordedStock.getSymbol(), recordedStock.getValue());
        }
        return stockPrices;
    }

    public double getUserFunds(User user) {
        User userRecord = databaseAPI.getUserRecord(user);
        return user.getAvailableFunds();
    }

    public double requestStockPrice(String symbol) throws Exception{
        return stockAPI.getCurrentPrice(symbol);
    }

    public void addStockToDatabase(User user, Stock stock){
        try{
            databaseAPI.addStockRecord(user.getId(), stock.getSymbol(), stock.getVolume(), stock.getValue());
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    public void sort() {
    }

    public void sortByHighest() {
    }

    public void sortByLowest() {
    }

    public void sellStock(int index) {
        databaseAPI.deleteStockRecord(index);
    }
}
