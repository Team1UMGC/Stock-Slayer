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

    /**
     * Initializes the database if not already initialized.
     * Ran each time the spring boot app runs.
     */
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
                        resultSet.getString("password"),
                        resultSet.getBoolean("isLocked"),
                        resultSet.getDouble("availableFunds")
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

    /**
     * Add User to database
     * @param email String, email, unique to each user
     * @param password String, password that the user creates for logging in
     * @throws Exception thrown when a matching email is already found in the database.
     */
    public void addUserRecord(String email, String password) throws Exception{
        if(getUserRecord(new User(email, password)) != null) throw new Exception("User already in database");

        final String addSql = "INSERT INTO user (email, password) VALUES (?, ?)";
        Object[] params = {email, password};
        int[] types = {Types.VARCHAR, Types.VARCHAR};

        int row = this.jdbcTemplate.update(addSql, params, types);
        System.out.printf("Added User: %s%n", email);
    }

    /**
     * Add User to database
     * @param user User, user object used to add a new user to the database
     * @throws Exception thrown when a matching email is already found in the database.
     */
    public void addUserRecord(User user) throws Exception{
        String userEmail = user.getEmail();
        String userPassword = user.getPassword();
        addUserRecord(userEmail, userPassword);
    }

    /**
     * Delete User from the database
     * @param userId int, ID of the user that is going to be deleted
     */
    public void deleteUserRecord(int userId){
        final String deleteSql = "DELETE FROM user WHERE id=?";
        Object[] params = {userId};
        int[] types = {Types.INTEGER};
        int rows = this.jdbcTemplate.update(deleteSql, params, types);
        deleteUserStocks(userId);
        System.out.printf("Deleted User: %s%n", userId);
    }

    /**
     * Get a single user record from the database
     * @param user User, user that is being retrieved from the database
     * @return returns User object from database or returns null if no user is found
     */
    public User getUserRecord(User user){ // TODO, needs to be an sql execution to find a user record for optimization
        User foundUser = null;
        List<User> recordedUsers = getUserTableInfo();
        for(User recordedUser : recordedUsers){
            if(Objects.equals(recordedUser.getEmail(), user.getEmail())) {
                foundUser = recordedUser;
                break;
            }
        }

        return foundUser;
    }

    public User getUserRecord(int userId) throws Exception {
        List<User> users = jdbcTemplate.query("SELECT * FROM user WHERE id="+userId,
                (resultSet, rowNum) -> new User(
                        resultSet.getInt("id"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isLocked"),
                        resultSet.getDouble("availableFunds")
                )
        );
        if(users.isEmpty()) throw new Exception("User not found!");
        return users.get(0);
    }

    /**
     * FIXME sql syntax needs to allow for updating, currently incorrect syntax
     * @param oldUser Old user information that is going to be replaced
     * @param newUser New user information that is going to replace the old user
     */
    public void updateUserRecord(User oldUser, User newUser){
        final String updateSql = "UPDATE user (email, password, isLocked) SET(?, ?, ?) WHERE id = ?";
        Object[] params = {newUser.getEmail(), newUser.getPassword(), newUser.getIsLocked(), oldUser.getId()};
        Object[] types = {Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.INTEGER};
        int rows = this.jdbcTemplate.update(updateSql, params, types);
        System.out.printf("Updated User: %1$s %n%2$s %n", oldUser.getId(), newUser);
    }

    /**
     * Pairs Users to their owned stocks so that the User object will contain list of stock objects
     * @param users List of users that will be paired
     * @param stocks List of stocks that will be paired
     * @return List of Users that have their stocks field filled out with their owned stocks from the database
     */
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

    /**
     * Adds Stock record to the database
     * @param ownerId int, ID of the owner of the stock
     * @param symbol String, symbol of the stock
     * @param volume double, number of shares purchased
     * @param value double, value of each share of the stock
     * @throws Exception thrown if a User does not exist that could own the stock in the database
     */
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

    /**
     * Adds Stock record to the database
     * @param stock Stock, stock object that will be added to the database
     * @throws Exception thrown if a User does not exist that could own the stock in the database
     */
    public void addStockRecord(Stock stock) throws Exception{
        addStockRecord(stock.getOwnerId(),
                stock.getSymbol(),
                stock.getVolume(),
                stock.getValue());
    }

    /**
     * Deletes a stock record from the database
     * @param stockId int, id of the stock that is going to be deleted
     */
    public void deleteStockRecord(int stockId) {
        jdbcTemplate.execute(String.format("DELETE FROM stock WHERE id='%s';", stockId));
        System.out.printf("Deleted Stock w/ id: %s%n", stockId);
    }

    public Stock getStockRecord(int stockId) throws Exception{
        List<Stock> stocks = jdbcTemplate.query("SELECT * FROM stock WHERE id=" + stockId,
            (resultSet, rowNum) -> new Stock(
                resultSet.getInt("id"),
                resultSet.getInt("ownerId"),
                resultSet.getString("symbol"),
                resultSet.getDouble("volume"),
                resultSet.getDouble("value")
            ));

        if(stocks.isEmpty()) throw new Exception("No stock with given ID was found!");

        return stocks.get(0);
    }

    /**
     * Returns a list of all users in the database
     * @return List, list of all users in the database in the form of User objects
     */
    public List<User> getUserTableInfo(){
        String selectSql = "SELECT * FROM user";
        return queryForUsers(selectSql);
    }

    /**
     * Returns a list of all stocks in the database
     * @return List, list of all stocks in the database in the form of Stock objects
     */
    public List<Stock> getStockTableInfo() {
        String selectSql = "SELECT * FROM stock";
        return queryForStocks(selectSql);
    }

    /**
     * Adds funds to a user in the database
     * @param user User, user that will be receiving additional funds
     * @param funds double, amount to be added
     */
    public void addAvailableFunds(User user, double funds){
        User userRecord = getUserRecord(user);
        userRecord.addFunds(funds);
        jdbcTemplate.execute(String.format("UPDATE user SET availableFunds=%1$f WHERE id=%2$s;",
                userRecord.getAvailableFunds(), userRecord.getId()));
        System.out.printf("Set availableFunds %1$s to user %2$s", userRecord.getAvailableFunds(), user.getEmail());
    }

    /**
     * Subtract funds from a user in the database
     * @param user User, user that will be receiving subtracted funds
     * @param funds double, amount to be subtracted
     */
    public void subtractAvailableFunds(User user, double funds){
        User userRecord = getUserRecord(user);
        userRecord.subtractFunds(funds);
        jdbcTemplate.execute(String.format("UPDATE user SET availableFunds=%1$f WHERE id=%2$s;",
                userRecord.getAvailableFunds(), userRecord.getId()));
        System.out.printf("Set availableFunds %1$s to user %2$s", userRecord.getAvailableFunds(), user.getEmail());
    }

    /**
     * Toggles a user's isLocked field. If locked, user cannot log in. Otherwise, user can log in.
     * @param user User to have the isLocked field toggled
     */
    public void toggleUserLocked(User user){
        User userRecord = getUserRecord(user);
        userRecord.toggleLock();
        jdbcTemplate.execute(String.format("UPDATE user SET isLocked=%1$s WHERE id='%2$s'",
                userRecord.getIsLocked(), userRecord.getId()));
        System.out.printf("isLocked set to: %1$s user account: %2$s", userRecord.getIsLocked(), userRecord.getEmail());
    }

    /**
     * Deletes all stocks that belong to a user
     * @param userId ID of the user whose stocks will be deleted
     */
    private void deleteUserStocks(int userId){
        jdbcTemplate.execute(String.format("DELETE FROM stock WHERE ownerId='%s';", userId));
        System.out.printf("Deleted User: %s stocks%n", userId);
    }

    /**
     *
     * @param query
     * @return
     * TODO, sql query could be implemented in the method, param does not need to be passed
     */
    private List<User> queryForUsers(String query){
        List<User> users = jdbcTemplate.query(query,
            (resultSet, rowNum) -> new User(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getBoolean("isLocked"),
                resultSet.getDouble("availableFunds")
            )
        );
        users = pairUsersToStocks(users, getStockTableInfo());
        return users;
    }

    /**
     *
     * @param query
     * @return
     * TODO, sql query could be implemented in the method, param does not need to be passed
     */
    private List<Stock> queryForStocks(String query){
        return jdbcTemplate.query(query,
            (resultSet, rowNum) -> new Stock(
                resultSet.getInt("id"),
                resultSet.getInt("ownerId"),
                resultSet.getString("symbol"),
                resultSet.getDouble("volume"),
                resultSet.getDouble("value")
            )
        );
    }

    /**
     *
     */
    @EventListener(ApplicationReadyEvent.class)
    public void printDatabaseAPIStart() {
        System.out.println("Database API Running...");
    }

    @Override
    public void run(String... args) throws Exception {

    }

}

