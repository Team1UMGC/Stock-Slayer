package com.groupone.api;

import com.groupone.model.Stock;
import com.groupone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.List;

@Component
public class DatabaseAPI implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void initDatabase(){
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user(" +
                "email STRING NOT NULL," +
                "password STRING NOT NULL," +
                "availableFunds DOUBLE," + // TODO reset table for availableFunds column
                "isLocked BOOLEAN);");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS stock(" +
                "ownerId INTEGER NOT NULL," +
                "symbol STRING NOT NULL," +
                "volume DOUBLE," +
                "value DOUBLE," +
                "FOREIGN KEY(ownerId) REFERENCES user(ROWID))");

        final boolean deleteTables = false;
        if(deleteTables){
            jdbcTemplate.execute("DROP TABLE IF EXISTS user");
            jdbcTemplate.execute("DROP TABLE IF EXISTS stock");
        }


//        jdbcTemplate.execute("INSERT INTO user (email, password) VALUES('demo@example.com', 'password');");
//        jdbcTemplate.execute("INSERT INTO user (email, password) VALUES('example@demo.com', 'p@ssw0rd');");
        List<User> users = jdbcTemplate.query("SELECT * FROM user;",
                (resultSet, rowNum) -> new User(
                        rowNum,
                        resultSet.getString("email"),
                        resultSet.getString("password")
                ));

//        jdbcTemplate.execute("INSERT INTO stock (ownerId, symbol, volume, value) VALUES (0, 'IBM', 140.3, 23.21);");
//        jdbcTemplate.execute("INSERT INTO stock (ownerId, symbol, volume, value) VALUES (1, 'APPL', 140.3, 23.21);");
        List<Stock> stocks = jdbcTemplate.query("SELECT * FROM stock;",
                (resultSet, rowNum) -> new Stock(
                        rowNum,
                        resultSet.getInt("ownerId"),
                        resultSet.getString("symbol"),
                        resultSet.getDouble("volume"),
                        resultSet.getDouble("value")
                ));


        users = pairUsersToStocks(users, stocks);


        users.forEach(System.out::println);
        stocks.forEach(System.out::println);
    }

    public void addUserRecord(String email, String password){
        final String addSql = "INSERT INTO user (email, password) VALUES (?, ?)";
        Object[] params = {email, password};
        int[] types = {Types.VARCHAR, Types.VARCHAR};

        int row = this.jdbcTemplate.update(addSql, params, types);
        System.out.printf("Added User: %s%n", email);
    }

    public void deleteUserRecord(String email){
        final String deleteSql = "DELETE FROM user WHERE email=?";
        Object[] params = {email};
        int[] types = {Types.VARCHAR};
        int rows = this.jdbcTemplate.update(deleteSql, params, types);
        System.out.printf("Deleted User: %s%n", email);
    }

    public void updateUserRecord(User oldUser, User newUser){
        final String updateSql = "UPDATE user (email, password, isLocked) SET(?, ?, ?) WHERE id = ?";
        Object[] params = {newUser.getEmail(), newUser.getPassword(), newUser.getIsLocked(), oldUser.getID()};
        Object[] types = {Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.INTEGER};
        int rows = this.jdbcTemplate.update(updateSql, params, types);
        System.out.printf("Updated User: %1$s %n%2$s", oldUser.getID(), newUser);
    }

    public List<User> pairUsersToStocks(List<User> users, List<Stock> stocks){
        users.forEach(user -> {
            stocks.forEach(stock -> {
                if(user.getID() == stock.getOwnerId()){
                    user.addStock(stock);
                }
            });
        });
        return users;
    }

    public void addStockRecord(Stock stock){
        // TODO make method body
    }

    public void deleteStockRecord(int stockId) {
        // TODO make method body
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printDatabaseAPIStart() {
        System.out.println("Database API Running...");
    }

    @Override
    public void run(String... args) throws Exception {

    }

}

