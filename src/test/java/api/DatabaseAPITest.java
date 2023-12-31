package api;

import com.groupone.StockSlayerApplication;
import com.groupone.api.DatabaseAPI;
import com.groupone.exception.UserAlreadyExistsException;
import com.groupone.exception.UserNotFoundException;
import com.groupone.exception.UserUpdateFailedException;
import com.groupone.model.Stock;
import com.groupone.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests methods from DatabaseAPI using JUnit 5
 */
@SpringBootTest(classes = {StockSlayerApplication.class})
public class DatabaseAPITest {

    @Autowired
    DatabaseAPI databaseAPI;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static User user;
    static Stock stock;
    static File dbFile;

    /**
     * Creates a new user object and stock ArrayList, then inserts
     */
    @BeforeEach
    void setUp(){
        ArrayList<Stock> stocks = new ArrayList<>();
        stocks.add(new Stock(1, 1, "IBM", 100.0, 123.1));
        user = new User(1,
                "testUser@test.com",
                "p@ssw0rd",
                false,
                0.0,
                stocks
        );
        dbFile = new File("slayer.db");
        stock = stocks.get(0);

        // Checks if the test case user is already in the database, if so, delete it
        List<User> userList = selectFromUserWhereEmail(user.getEmail());

        if(!userList.isEmpty()){
            User tempUser = userList.get(0);
            jdbcTemplate.execute("DELETE FROM stock WHERE ownerId="+tempUser.getId());
            jdbcTemplate.execute("DELETE FROM user WHERE id="+tempUser.getId());
        }

        // Insert test case user to the database
        jdbcTemplate.execute(String.format("INSERT INTO user (email, password, isLocked, availableFunds) " +
            "VALUES ('%1$s', '%2$s', '%3$b', '%4$f')",
            user.getEmail(),
            user.getPassword(),
            user.getIsLocked(),
            user.getAvailableFunds()
        ));

        userList = selectFromUserWhereEmail(user.getEmail());

        jdbcTemplate.execute(String.format("INSERT INTO stock (ownerId, symbol, volume, value) " +
            "VALUES ('%1$d', '%2$s', '%3$f', '%4$f')",
            userList.get(0).getId(),
            stocks.get(0).getSymbol(),
            stocks.get(0).getVolume(),
            stocks.get(0).getValue()
        ));
    }

    @AfterEach
    void tearDown() {
        deleteSetUpDatabaseEntry();
    }

    /**
     * Tests database initialization on application start up.
     * It should connect to the database and print out all
     * users and stocks in the database upon launch for debugging purposes.
     */
    @Test
    void initDatabaseTest(){
        databaseAPI.initDatabase();
        assert dbFile.exists();
        assert !selectFromUserWhereEmail(user.getEmail()).isEmpty()
                : "User database is expected to be populated with at least one value before each test";
    }

    /**
     * Tests adding a new user into the database.
     * This should allow users to add new accounts to the application
     * then store their information in the database.
     * @throws UserAlreadyExistsException Expecting to be thrown if a matching email is found in the database.
     */
    @Test
    void addUserRecordTest() throws UserAlreadyExistsException {
        if(!selectFromUserWhereEmail(user.getEmail()).isEmpty()){
            deleteSetUpDatabaseEntry();
        }

        String email = user.getEmail();
        String password = user.getPassword();
        databaseAPI.addUserRecord(email, password);
        List<User> users = queryForUsers();

        assert !users.isEmpty() : "user table should be populated with at least one value";
        assert Objects.equals(selectFromUserWhereEmail(user.getEmail()).get(0).getEmail(), user.getEmail())
                : "user table should be populated with the following email : "+
                user.getEmail()+"\nReceived : "+selectFromUserWhereEmail(user.getEmail()).get(0).getEmail();

        assertThrows(UserAlreadyExistsException.class, () -> {
            databaseAPI.addUserRecord(email, password);
        }, "Expecting to throw an exception that the user is already register in the database");

    }

    /**
     * Tests updating an already existing user in the database with a new created user object.
     * This is intended for updating a forgotten password, or changing emails. But changing other fields
     * for any reason is permitted as needed.
     * @throws UserNotFoundException Thrown if the user does not exist in the database
     * @throws UserUpdateFailedException Thrown if updating the user's information in the database
     * has failed for any reason
     */
    @Test
    void updateUserRecordTest() throws UserNotFoundException, UserUpdateFailedException {
        User oldUpdate;
        User updatedUser = new User("testUpdatedUser@updated.com", "updatedPassword");

        oldUpdate = databaseAPI.getUserRecord(user.getEmail());
        databaseAPI.updateUserRecord(oldUpdate.getId(), updatedUser);

        assertThrows(Exception.class, () -> {
            databaseAPI.getUserRecord(user.getEmail());
        }, "Expected to not find the old record that was just updated");

        assert Objects.equals(databaseAPI.getUserRecord(updatedUser.getEmail()).getEmail(), updatedUser.getEmail())
                : "Expect database entry updated email to match local updated email";
    }

    /**
     * Tests adding a stock record to the database to the stock table.
     * Used when purchasing a stock and attaching the stock to a user as to symbol ownership.
     * @throws UserNotFoundException Thrown if the user does not exist in the database
     */
    @Test
    void addStockRecordTest() throws UserNotFoundException {
        if(!selectFromStockWhereOwnerId(selectFromUserWhereEmail(user.getEmail()).get(0).getId()).isEmpty()){
            jdbcTemplate.execute("DELETE FROM stock WHERE ownerId="+selectFromUserWhereEmail(user.getEmail()).get(0).getId());
        }

        int ownerId = selectFromUserWhereEmail(user.getEmail()).get(0).getId();
        String symbol = stock.getSymbol();
        double volume = stock.getVolume();
        double value = stock.getValue();

        databaseAPI.addStockRecord(ownerId, symbol, volume, value);
        List<Stock> stocks = selectFromStockWhereOwnerId(selectFromUserWhereEmail(user.getEmail()).get(0).getId());

        assert !stocks.isEmpty() : "Stocks table should have at least one value inserted";
        assert stocks.get(0).getOwnerId() == ownerId : "Owner ID should match the User ID";

        assertThrows(UserNotFoundException.class, () -> {
            databaseAPI.addStockRecord(-1, symbol, volume, value);
        }, "There should be no owner with an ID of " + -1);
    }

    /**
     *
     * @throws UserNotFoundException Thrown if the user does not exist in the database
     */
    @Test
    void pairUsersToStocksTest() throws UserNotFoundException {
        User userRecord = databaseAPI.getUserRecord(user.getEmail());
        Stock userStock = userRecord.getStocks().get(0);
        assert Objects.equals(userStock.getSymbol(), stock.getSymbol())
                : "Stock symbols should match";

        List<User> usersPaired = databaseAPI.pairUsersToStocks(queryForUsers(), queryForStocks());
        User userPaired = usersPaired.get(0);

        assert !userPaired.getStocks().isEmpty()
                : "User should now be paired with a stock object";
        assert (queryForStocks().get(userPaired.getStocks().get(0).getId()-1).getOwnerId() == userPaired.getId())
                : "The paired stock should belong to the user with user ID "+userRecord.getId();
    }

    /**
     *
     */
    @Test
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

    /**
     *
     */
    @Test
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

    /**
     *
     * @throws UserNotFoundException
     */
    @Test
    void getUserRecordTest() throws UserNotFoundException{
        // Use ID for getUserRecord
        User userRecordWithId = databaseAPI.getUserRecord(selectFromUserWhereEmail(user.getEmail()).get(0).getId());
        assertNotNull(userRecordWithId, "The retrieved user is not expected to be null");
        assert Objects.equals(userRecordWithId.getEmail(), user.getEmail())
                : "The retrieved user should have matching emails";

        // Use Email for getUserRecord
        User userRecordWithEmail = databaseAPI.getUserRecord(user.getEmail());
        assertNotNull(userRecordWithEmail, "The retrieved user is not expected to be null");
        assert Objects.equals(userRecordWithEmail.getEmail(), user.getEmail())
                : "The retrieved user should have matching emails";
    }

    /**
     *
     * @throws UserNotFoundException
     */
    @Test
    void addAvailableFundsTest() throws UserNotFoundException {
        User userRecordBeforeAddition = selectFromUserWhereEmail(user.getEmail()).get(0);
        databaseAPI.addAvailableFunds(userRecordBeforeAddition, 20.00);
        User userRecordAfterAddition = selectFromUserWhereEmail(user.getEmail()).get(0);
        assert userRecordAfterAddition.getAvailableFunds() > userRecordBeforeAddition.getAvailableFunds();
    }

    /**
     *
     * @throws UserNotFoundException
     */
    @Test
    void subtractAvailableFundsTest() throws UserNotFoundException {
        User userRecordBeforeSubtraction = selectFromUserWhereEmail(user.getEmail()).get(0);
        databaseAPI.subtractAvailableFunds(userRecordBeforeSubtraction, 20.00);
        User userRecordAfterSubtraction = selectFromUserWhereEmail(user.getEmail()).get(0);
        assert userRecordAfterSubtraction.getAvailableFunds() < userRecordBeforeSubtraction.getAvailableFunds();
    }

    /**
     *
     * @throws UserNotFoundException
     */
    @Test
    void toggleUserLockedTest() throws UserNotFoundException {
        User userRecordBeforeToggle = selectFromUserWhereEmail(user.getEmail()).get(0);
        databaseAPI.toggleUserLocked(userRecordBeforeToggle);
        User userRecordAfterToggle = selectFromUserWhereEmail(user.getEmail()).get(0);
        assert userRecordBeforeToggle.getIsLocked() != userRecordAfterToggle.getIsLocked();
    }

    /**
     *
     */
    @Test
    void deleteStockRecordTest(){
        databaseAPI.deleteStockRecord(selectFromUserWhereEmail(user.getEmail()).get(0).getId());
        User userAfterStockDeleted = selectFromUserWhereEmail(user.getEmail()).get(0);
        assert userAfterStockDeleted.getStocks().isEmpty()
                : "Stock added to test user should have been deleted";
    }

    /**
     *
     * @throws UserNotFoundException
     */
    @Test
    void deleteUserRecordTest() throws UserNotFoundException {
        User userInfoBeforeDeletion = selectFromUserWhereEmail(user.getEmail()).get(0);

        databaseAPI.deleteUserRecord(user.getEmail());
        assert selectFromUserWhereEmail(user.getEmail()).isEmpty()
                : "User test record should have been deleted";

        List<Stock> userStocks = selectFromStockWhereOwnerId(userInfoBeforeDeletion.getId());
        assert userStocks.isEmpty() : "User test record's stocks should have been deleted";
    }

    /**
     *
     * @param email
     * @return
     */
    private List<User> selectFromUserWhereEmail(String email){
        return jdbcTemplate.query(String.format("SELECT * FROM user WHERE email='%s'", email),
            (resultSet, rowNum) -> new User(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getBoolean("isLocked"),
                resultSet.getDouble("availableFunds")
            )
        );
    }

    /**
     *
     * @param ownerId
     * @return
     */
    private List<Stock> selectFromStockWhereOwnerId(int ownerId){
        return jdbcTemplate.query(String.format("SELECT * FROM stock WHERE ownerId='%d'", ownerId),
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
    private void deleteSetUpDatabaseEntry(){
        List<User> userList = selectFromUserWhereEmail(user.getEmail());
        if(!userList.isEmpty()){
            jdbcTemplate.execute("DELETE FROM stock WHERE ownerId="+userList.get(0).getId());
            jdbcTemplate.execute("DELETE FROM user WHERE id="+userList.get(0).getId());
        }
    }

    /**
     *
     * @return
     */
    private List<User> queryForUsers(){
        List<User> users = jdbcTemplate.query("SELECT * FROM user;",
            (resultSet, rowNum) -> new User(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getBoolean("isLocked"),
                resultSet.getDouble("availableFunds")
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

    /**
     *
     * @return
     */
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
