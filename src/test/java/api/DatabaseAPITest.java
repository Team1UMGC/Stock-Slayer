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

//TODO add addAvailableFundsTest & subtractAvailableFundsTest methods
//TODO add toggleUserLockedTest method
@SpringBootTest(classes = {StockSlayerApplication.class})
public class DatabaseAPITest {

    @Autowired
    DatabaseAPI databaseAPI;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static User user;
    static Stock stock;
    static File dbFile;

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
        dbFile = new File("slayer.db");
        stock = stocks.get(0);
    }

    @Test
    @Order(1)
    void initDatabaseTest(){
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

        // TODO, create code that checks what IDs that are in use, then, randomly select one not in use.
        int notValidOwnerId = 99999;

        assertThrows(Exception.class, () -> {
            databaseAPI.addStockRecord(notValidOwnerId, symbol, volume, value);
        }, "There should be no owner with an ID of " + notValidOwnerId);
    }

    @Test
    @Order(4)
    void pairUsersToStocksTest(){
        databaseAPI.addUserRecord(user);
        try{
            databaseAPI.addStockRecord(stock);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

        List<User> usersPaired = databaseAPI.pairUsersToStocks(queryForUsers(), queryForStocks());

        User userPaired = usersPaired.get(0);
        assert !userPaired.getStocks().isEmpty() : "User should now be paired with a stock object";
        // FIXME This should be done in a way that checks all users are paired, not just the first user
        assert (userPaired.getStocks().get(0).getOwnerId() == queryForStocks().get(0).getOwnerId()) : "The paired stock should belong to the user with user ID 1";
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

    @Test
    @Order(7)
    void getUserRecordTest(){

        User userRecord = databaseAPI.getUserRecord(user);
        assertNotNull(userRecord, "The retrieved user is not expected to be null");
        assert Objects.equals(userRecord.getEmail(), user.getEmail()) : "The retrieved user should have matching emails";
    }

    @Test
    @Order(8)
    void deleteStockRecordTest(){
        List<Stock> localStocksQuery = queryForStocks();
        for (int i = 1; i <= localStocksQuery.size(); i++) {
            databaseAPI.deleteStockRecord(i);
        }
        localStocksQuery = queryForStocks();
        assert localStocksQuery.isEmpty() : "Stock table is expected to be empty";
    }

    @Test
    @Order(9)
    void deleteUserRecordTest(){
        List<User> localUserQuery = queryForUsers();
        for (int i = 1; i <= localUserQuery.size(); i++) {
            databaseAPI.deleteUserRecord(i);
        }
        localUserQuery = queryForUsers();
        assert localUserQuery.isEmpty() : "User table is expected to be empty";
        assert queryForStocks().isEmpty() : "Stock table is expected to be empty";
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
