package com.groupone.service;

import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.groupone.dao.StockAPI;
import com.groupone.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class StockService {
    @Autowired
    StockAPI stockAPI;
    public List<Stock> getHeldStocks() {
        //TODO write method body
        return null;
    }

    public Map<String, Double> getStockPrices() {
        //TODO write method body
        return null;
    }

    public double getUserFunds() {
        //TODO write method body
        return 0.0;
    }

    public StockUnit requestStock(String symbol) throws Exception{
        //TODO write method body
        return null;
    }

    public void sort() {
    }

    public void sortByHighest() {
    }

    public void sortByLowest() {
    }

    public void sellStock(int index) {
    }
}
