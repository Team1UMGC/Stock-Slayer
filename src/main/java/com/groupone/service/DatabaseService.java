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

    public List<User> getUserTableInfo(){
        return databaseAPI.getUserTableInfo();
    }

    public List<Stock> getStockTableInfo() {
        return databaseAPI.getStockTableInfo();
    }

    public User getUserRecord(User user){
        return databaseAPI.getUserRecord(user);
    }

    public void deleteUserRecord(int userId){
        databaseAPI.deleteUserRecord(userId);
    }

    public void deleteStockRecord(int stockId) {
        databaseAPI.deleteStockRecord(stockId);
    }

    public void addUserRecord(String email, String password) {
        try {
            databaseAPI.addUserRecord(email, password);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public void addStockRecord(int ownerId, String symbol, Double volume, Double value) {
        try{
            databaseAPI.addStockRecord(ownerId, symbol, volume, value);
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
//
//    public User getUser(int userid) throws Exception{
//        String selectSql = "SELECT * FROM user WHERE id=" + userid;
//        List<User> result = queryForUsers(selectSql);
//        if(result.isEmpty()) throw new Exception("User Not Found!");
//        return result.get(0);
//    }


}
