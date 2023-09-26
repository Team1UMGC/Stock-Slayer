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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DatabaseAPI databaseAPI;

    public List<User> getUserTableInfo(){
        String selectSql = "SELECT * FROM user";
        return queryForUsers(selectSql);
    }

    public User getUser(int userid) throws Exception{
        String selectSql = "SELECT * FROM user WHERE ROWID=" + userid;
        List<User> result = queryForUsers(selectSql);
        if(result.isEmpty()) throw new Exception("User Not Found!");
        return result.get(0);
    }

    private List<User> queryForUsers(String query){
        List<User> users = jdbcTemplate.query(query,
                (resultSet, rowNum) -> new User(
                    rowNum,
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getBoolean("isLocked")
        ));

        return databaseAPI.pairUsersToStocks(users, getStockTableInfo());
    }

    private List<Stock> queryForStocks(String query){
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Stock(
                   rowNum,
                   resultSet.getInt("ownerId"),
                   resultSet.getString("symbol"),
                   resultSet.getDouble("volume"),
                   resultSet.getDouble("value")
                ));
    }

    public List<Stock> getStockTableInfo() {
        String selectSql = "SELECT * FROM stock";
        return queryForStocks(selectSql);
    }
}
