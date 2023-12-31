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

/**
 * This service interacts with both the databaseAPI and StockAPI in regard to stocks,
 * and handles requests to the Alpha Vantage API. If changes to stock data or requests for stock data
 * need to happen, this is the service object to be used.
 */
@Service
public class StockService {
    @Autowired
    StockAPI stockAPI;

    @Autowired
    DatabaseAPI databaseAPI;

    /**
     * Gets a list of all held stocks of the passed user
     * @param user User, finds the held stocks of that user
     * @return List containing Stock objects all owned by that user
     */
    public List<Stock> getHeldStocks(User user) throws Exception {
        User userRecord = databaseAPI.getUserRecord(user.getId());
        return userRecord.getStocks();
    }

    /**
     * Creates a map object with the symbol the of the owned stock as the key, and the value of the stock.
     * @param user User, finds the held stocks and values of that user
     * @return Map<String, Double> Returns map, String key is symbol of that stock, Double value is the value of that stock.
     */
    public Map<String, Double> getStockPrices(User user) throws Exception {
        User userRecord = databaseAPI.getUserRecord(user.getId());
        Map<String, Double> stockPrices = new HashMap<>(); // TODO, abstract this into databaseAPI as a method
        for (Stock recordedStock: userRecord.getStocks()) {
            stockPrices.put(recordedStock.getSymbol(), recordedStock.getValue());
        }
        return stockPrices;
    }

    /**
     * Returns the user's available funds
     * @param user User that is being checked to return their available funds
     * @return double, the available funds that a passed user has
     */
    public double getUserFunds(User user) throws Exception {
        return databaseAPI.getUserRecord(user.getId()).getAvailableFunds();
    }

    public void subtractUserFunds(User user, double value) throws Exception {
        databaseAPI.subtractAvailableFunds(user, value);
    }

    /**
     * Sends request to Alpha Vantage API to get the current price of the stock from passed symbol
     * @param symbol String, symbol of the stock
     * @return double, the current price of the stock passed from the param
     * @throws Exception
     */
    public double requestStockPrice(String symbol) throws Exception{
        return stockAPI.getCurrentPrice(symbol);
    }

    /**
     * Adds a new stock record to the database with given params of the user and stock
     * @param user User, the user that will own this stock
     * @param stock Stock, the information about the stock that is going to be added
     */
    public void addStockToDatabase(User user, Stock stock){
        try{
            databaseAPI.addStockRecord(user.getId(), stock.getSymbol(), stock.getVolume(), stock.getValue());
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Calls to databaseAPI using helper methods to add a new stock to a user's account
     * and subtracts funds from the user.
     * @param user User, specified user who shall have an added stock, and subtracted funds
     * @param stock Stock, the passed stock object that the user shall have added to their account
     * @throws Exception Thrown if the totalCost is > what the user has in funds,
     *                   or if the volume given is <= 0.
     */
    public void purchaseStock(User user, Stock stock) throws Exception{
        double totalCost = stock.getValue() * stock.getVolume();
        if(getUserFunds(user) < totalCost) throw new Exception("Total cost to purchase stock is greater than user's available funds");
        if(stock.getVolume() <= 0) throw new Exception("Inputted volume is less than or equal to zero");

        user.subtractFunds(totalCost);
        user.addStock(stock.getSymbol(), stock.getVolume(), stock.getValue());
        addStockToDatabase(user, new Stock(stock.getSymbol(), stock.getValue(), stock.getVolume()));
        subtractUserFunds(user, totalCost);
    }

    /**
     * Calls to databaseAPI to sell a stock. Which involves deleting the stock from the user's account,
     * and then adding funds to a user's account.
     * @param user User, specified user who shall be selling the stock
     * @param stockId int, ID of the stock that is going to be sold
     */
    public void sellStock(User user, int stockId) throws Exception {
        Stock stock = null;
        stock = databaseAPI.getStockRecord(stockId);
        databaseAPI.addAvailableFunds(user, requestCloseValue(stock.getSymbol()) * stock.getVolume());
        databaseAPI.deleteStockRecord(stockId);
    }

    /**
     * Helper method that calls stockAPI to get the current close price when selling a stock
     * @param symbol String, the symbol the stock that is going to be requested
     * @return double The current price of the stock
     * @throws Exception Thrown if an error occurred when trying to request information from Alpha Vantage
     */
    private double requestCloseValue(String symbol) throws Exception {
        return stockAPI.getCurrentPrice(symbol);
    }

    public void sort() {
        // TODO, make method body
    }

    public void sortByHighest() {
        // TODO, make method body
    }

    public void sortByLowest() {
        // TODO, make method body
    }

}
