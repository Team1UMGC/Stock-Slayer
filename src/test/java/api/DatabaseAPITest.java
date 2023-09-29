package api;

import com.groupone.StockSlayerApplication;
import com.groupone.api.DatabaseAPI;
import com.groupone.model.Stock;
import com.groupone.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {StockSlayerApplication.class})
public class DatabaseAPITest {

    @Autowired
    DatabaseAPI databaseAPI;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static User user;

    @BeforeAll
    static void setUp(){
        ArrayList<Stock> stocks = new ArrayList<>();
        stocks.add(new Stock(1, 1, "IBM", 100.0, 123.1));
        user = new User(1,
                "test@gmail.com",
                "p@ssw0rd",
                false,
                1_000.00,
                stocks
        );
    }

    @Test
    @Order(1)
    void initDatabaseTest(){
        File dbFile = new File("slayer.db");
        boolean isDeleted = dbFile.delete();
        if(isDeleted){
            System.out.println("slayer.db was deleted. Calling initDatabase()...");
        } else {
            System.out.println("slayer.db not found or could not be deleted! Calling initDatabase()...");
        }
        databaseAPI.initDatabase();
        assert dbFile.exists();
    }

    @Test
    @Order(2)
    void addUserRecordTest(){
        String email = user.getEmail();
        String password = user.getPassword();
        databaseAPI.addUserRecord(email, password);
        List<User> users = queryForUsers();

        assert !users.isEmpty() : "user table should be populated with at least one value";

        boolean userFound = false;
        for (User user: users) {
            if (Objects.equals(user.getEmail(), email)
                    && Objects.equals(user.getPassword(), password)) {
                userFound = true;
                break;
            }
        }

        assert userFound : "could not find the user that was just added to slayer.db";

    }

    @Test
    @Order(3)
    void addStockRecordTest(){
        int ownerId = user.getStocks().get(0).getOwnerId();
        String symbol = user.getStocks().get(0).getSymbol();
        double volume = user.getStocks().get(0).getVolume();
        double value = user.getStocks().get(0).getValue();

        try{
            databaseAPI.addStockRecord(ownerId, symbol, volume, value);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

        List<Stock> stocks = queryForStocks();

        assert !stocks.isEmpty() : "Stocks table should have at least one value inserted";

        boolean stockFound = false;
        for (Stock stock: stocks) {
            if (stock.getOwnerId() == ownerId && Objects.equals(stock.getSymbol(), symbol)) {
                stockFound = true;
                break;
            }
        }

        assert stockFound : "Should have a stock with the same ownerId and symbol as was inserted";

        int notValidOwnerId = 5;

        assertThrows(Exception.class, () -> {
            databaseAPI.addStockRecord(notValidOwnerId, symbol, volume, value);
        }, "There should be no owner with an ID of 5");
    }

    @Test
    @Order(4)
    void pairUsersToStocksTest(){
        List<User> usersPaired = databaseAPI.pairUsersToStocks(queryForUsers(), queryForStocks());

        User userPaired = usersPaired.get(0);
        assert !userPaired.getStocks().isEmpty() : "User should now be paired with a stock object";
        assert (userPaired.getStocks().get(0).getOwnerId() == 1) : "The paired stock should belong to the user with user ID 1";
    }

    @Test
    @Order(5)
    void getUserTableInfoTest(){
        List<User> users = databaseAPI.getUserTableInfo();
        List<User> localUsersQuery = queryForUsers();
        assertNotNull(users, "getUserTableInfo method should return a non null value");

        boolean isEqual = true;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getId() != localUsersQuery.get(i).getId()){
                isEqual = false;
                break;
            }
        }
        assert isEqual : "Local test query should match with called database API method query for users";
    }

    @Test
    @Order(6)
    void getStockTableInfoTest(){
        List<Stock> stocks = databaseAPI.getStockTableInfo();
        List<Stock> localStocksQuery = queryForStocks();
        assertNotNull(stocks, "getStockTableInfo method should return a non null value");

        boolean isEqual = true;
        for(int i = 0; i < stocks.size(); i++){
            if(stocks.get(i).getId() != localStocksQuery.get(i).getId()){
                isEqual = false;
                break;
            }
        }
        assert isEqual : "Local test query should match with called database API method query for stocks";
    }


    private List<User> queryForUsers(){
        List<User> users = jdbcTemplate.query("SELECT * FROM user;",
            (resultSet, rowNum) ->
                new User(
                        resultSet.getInt("id"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isLocked")
                )
        );

        users.forEach(user -> {
            queryForStocks().forEach(stock -> {
                if(user.getId() == stock.getOwnerId()){
                    user.addStock(stock);
                }
            });
        });

        return users;
    }

    private List<Stock> queryForStocks(){
        return jdbcTemplate.query("SELECT * FROM stock;",
            (resultSet, rowNum) -> new Stock(
                resultSet.getInt("id"),
                resultSet.getInt("ownerId"),
                resultSet.getString("symbol"),
                resultSet.getDouble("volume"),
                resultSet.getDouble("value")
            )
        );
    }
}
