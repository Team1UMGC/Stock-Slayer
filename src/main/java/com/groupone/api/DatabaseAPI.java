package com.groupone.api;

import com.groupone.model.Stock;
import com.groupone.model.User;
import com.groupone.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.List;
import java.util.Objects;

@Component
public class DatabaseAPI implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void initDatabase(){
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS user(" +
                "id             INTEGER PRIMARY KEY," +
                "email          STRING NOT NULL," +
                "password       STRING NOT NULL," +
                "availableFunds DOUBLE," +
                "isLocked       BOOLEAN);");

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS stock(" +
                "id         INTEGER PRIMARY KEY," +
                "ownerId    INTEGER NOT NULL REFERENCES user (id)," +
                "symbol     STRING NOT NULL," +
                "volume     DOUBLE," +
                "value      DOUBLE," +
                "FOREIGN KEY (ownerId) REFERENCES user (id));"
        );

        List<User> users = jdbcTemplate.query("SELECT * FROM user;",
                (resultSet, rowNum) -> new User(
                        resultSet.getInt("id"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                ));

        List<Stock> stocks = jdbcTemplate.query("SELECT * FROM stock;",
                (resultSet, rowNum) -> new Stock(
                        resultSet.getInt("id"),
                        resultSet.getInt("ownerId"),
                        resultSet.getString("symbol"),
                        resultSet.getDouble("volume"),
                        resultSet.getDouble("value")
                ));
        users = pairUsersToStocks(users, stocks);

        if(!users.isEmpty() || !stocks.isEmpty()){
            users.forEach(System.out::println);
            stocks.forEach(System.out::println);
        } else {
            System.out.println("No Records to Display");
        }
    }

    public void addUserRecord(String email, String password){
        final String addSql = "INSERT INTO user (email, password) VALUES (?, ?)";
        Object[] params = {email, password};
        int[] types = {Types.VARCHAR, Types.VARCHAR};

        int row = this.jdbcTemplate.update(addSql, params, types);
        System.out.printf("Added User: %s%n", email);
    }

    public void deleteUserRecord(int userId){
        final String deleteSql = "DELETE FROM user WHERE id=?";
        Object[] params = {userId};
        int[] types = {Types.INTEGER};
        int rows = this.jdbcTemplate.update(deleteSql, params, types);
        deleteUserStocks(userId);
        System.out.printf("Deleted User: %s%n", userId);
    }

    public User getUserRecord(User user){
        User foundUser = null;
        List<User> recordedUsers = pairUsersToStocks(getUserTableInfo(), getStockTableInfo());
        for(User recordedUser : recordedUsers){
            if(Objects.equals(recordedUser.getEmail(), user.getEmail())) {
                foundUser = user;
                break;
            }
        }

        return foundUser;
    }

    public void updateUserRecord(User oldUser, User newUser){
        final String updateSql = "UPDATE user (email, password, isLocked) SET(?, ?, ?) WHERE id = ?";
        Object[] params = {newUser.getEmail(), newUser.getPassword(), newUser.getIsLocked(), oldUser.getId()};
        Object[] types = {Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.INTEGER};
        int rows = this.jdbcTemplate.update(updateSql, params, types);
        System.out.printf("Updated User: %1$s %n%2$s %n", oldUser.getId(), newUser);
    }

    public List<User> pairUsersToStocks(List<User> users, List<Stock> stocks){
        users.forEach(user -> {
            stocks.forEach(stock -> {
                if(user.getId() == stock.getOwnerId()){
                    user.addStock(stock);
                }
            });
        });
        return users;
    }

    public void addStockRecord(int ownerId, String symbol, double volume, double value) throws Exception{
        List<User> users = getUserTableInfo();
        boolean isValidOwnedId = false;
        for (User user: users) {
            if(user.getId() == ownerId){
                isValidOwnedId = true;
                break;
            }
        }

        if(!isValidOwnedId) throw new Exception("Not Valid Owner ID!");

        final String addSql = String.format("INSERT INTO stock (ownerId, symbol, volume, value) VALUES ('%1$s', '%2$s', '%3$s', '%4$s');",
                ownerId, symbol, volume, value);
        this.jdbcTemplate.execute(addSql);
        System.out.printf("Added Stock to user: %s%n", ownerId);
    }

    public void deleteStockRecord(int stockId) {
        jdbcTemplate.execute(String.format("DELETE FROM stock WHERE id='%s';", stockId));
        System.out.printf("Deleted Stock w/ id: %s%n", stockId);
    }


    public List<User> getUserTableInfo(){
        String selectSql = "SELECT * FROM user";
        return queryForUsers(selectSql);
    }

    public List<Stock> getStockTableInfo() {
        String selectSql = "SELECT * FROM stock";
        return queryForStocks(selectSql);
    }

    private void deleteUserStocks(int userId){
        jdbcTemplate.execute(String.format("DELETE FROM stock WHERE ownerId='%s';", userId));
        System.out.printf("Deleted User: %s stocks%n", userId);
    }

    private List<User> queryForUsers(String query){
        List<User> users = jdbcTemplate.query(query,
                (resultSet, rowNum) -> new User(
                        resultSet.getInt("id"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isLocked")
                ));
        users = pairUsersToStocks(users, getStockTableInfo());
        return users;
    }

    private List<Stock> queryForStocks(String query){
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Stock(
                        resultSet.getInt("id"),
                        resultSet.getInt("ownerId"),
                        resultSet.getString("symbol"),
                        resultSet.getDouble("volume"),
                        resultSet.getDouble("value")
                ));
    }

    // TODO, add addAvailableFunds & subtractAvailableFunds methods

    @EventListener(ApplicationReadyEvent.class)
    public void printDatabaseAPIStart() {
        System.out.println("Database API Running...");
    }

    @Override
    public void run(String... args) throws Exception {

    }

}

