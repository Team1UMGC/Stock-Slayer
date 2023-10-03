package com.groupone.service;

import com.groupone.api.DatabaseAPI;
import com.groupone.model.Stock;
import com.groupone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Service
public class DatabaseService {
    @Autowired
    private DatabaseAPI databaseAPI;

    /**
     * Returns list of all users in the database
     * @return List, contains User objects collected from the database
     */
    public List<User> getUserTableInfo(){
        return databaseAPI.getUserTableInfo();
    }

    /**
     * Returns list of all stocks in the database
     * @return List, contains Stock objects collected from the database
     */
    public List<Stock> getStockTableInfo() {
        return databaseAPI.getStockTableInfo();
    }

    /**
     * Returns a user object from the database
     * @param user User to be retrieved from the database
     * @return User object that is retrieved from the database
     */
    public User getUserRecord(User user){
        return databaseAPI.getUserRecord(user);
    }

    /**
     * Deletes the record of the user from the database given a user ID
     * @param userId int, ID of the user that is going to be deleted from the database
     */
    public void deleteUserRecord(int userId){
        databaseAPI.deleteUserRecord(userId);
    }

    /**
     * Deletes the record of the stock from the database given a stock ID
     * @param stockId int, ID of the stock that is going to be deleted from the database
     */
    public void deleteStockRecord(int stockId) {
        databaseAPI.deleteStockRecord(stockId);
    }

    /**
     * Adds a new user record to the database
     * @param email String, email of the user that is going to be added to the database
     * @param password String, password of the user that is going to be added to the database
     */
    public void addUserRecord(String email, String password) {
        try {
            databaseAPI.addUserRecord(email, password);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Adds a new stock record to the database
     * @param ownerId int, ID of the owner of the stock
     * @param symbol String, symbol of the stock
     * @param volume double, volume of stock that has been purchased
     * @param value double, value of the stock that has been purchased
     */
    public void addStockRecord(int ownerId, String symbol, double volume, double value) {
        try{
            databaseAPI.addStockRecord(ownerId, symbol, volume, value);
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    public void addFunds(int userId, double funds) throws Exception {
        databaseAPI.addAvailableFunds(databaseAPI.getUserRecord(userId), funds);
    }
}
