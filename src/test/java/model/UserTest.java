package model;

import com.groupone.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UserTest {

    User user;
    Stock stockIBM;
    Stock stockAPPL;
    ArrayList<Stock> stocks = new ArrayList<>();
    @BeforeEach
    void setUp(){
        stockIBM = new Stock(1, 1, "IBM", 123.1, 123.1);
        stockAPPL = new Stock(2, 1, "APPL", 321.1, 321.1);
        stocks.add(stockIBM);
        stocks.add(stockAPPL);

        user = new User(1,
                "test@gmail.com",
                "p@ssW0rd",
                false,
                100.0,
                stocks);
    }

    @Test
    void addStockTest(){
        Stock stock1 = new Stock(3, 1, "GOOGL", 231.1, 231.1);
        ArrayList<Stock> stock2 = new ArrayList<>(List.of(new Stock(4, 1, "BROS", 123.4, 432.1)));
        user.addStock(stock1);
        user.addStock(stock2);


        assert user.getStocks().contains(stock1) : "Expected to contain stock object 1";
        assert user.getStocks().contains(stock2.get(0)) : "Expected to contain stock object 2";
    }

    @Test
    void updateUserTest(){
        String oldUserEmail = user.getEmail();
        String oldUserPassword = user.getPassword();
        String newUserEmail = "test@yahoo.com";
        String newUserPassword = "P4ssW@rd";
        user.updateUser(newUserEmail, newUserPassword);

        assert Objects.equals(user.getEmail(), newUserEmail) :
                "User Email String is expected to be changed to new email";
        assert Objects.equals(user.getPassword(), newUserPassword) :
                "User password String is expected to be changed to new email";
        assert !Objects.equals(user.getEmail(), oldUserEmail) :
                "New User Email String should not be equal to the old email";
        assert !Objects.equals(user.getPassword(), oldUserPassword) :
                "New User password String should not be equal to the old email";

    }

    @Test
    void getStocksBySymbolTest(){
        ArrayList<Stock> returnedStocks = user.getStocksBySymbol("IBM");
        ArrayList<Stock> emptyReturnedStocks = user.getStocksBySymbol("BROS");
        assert returnedStocks.contains(stockIBM) :
                "Expected to have IBM stock in returned stock by symbol";
        assert emptyReturnedStocks.isEmpty() :
                "Expected to be empty since user does not contain BROS stock";
    }

    @Test
    void removeStockTest(){
        user.removeStock(1);
        ArrayList<Stock> ibmStocks = user.getStocksBySymbol("IBM");
        assert ibmStocks.isEmpty() :
                "Expecting that the IBM stock was removed from the user object";
    }

    @Test
    void toggleLockTest(){
        user.toggleLock();
        assert user.getIsLocked() : "User isLocked boolean should be toggled to true";

        user.toggleLock();
        assert !user.getIsLocked() : "User isLocked boolean should be toggled to false";
    }

    @Test
    void addFundsTest(){
        user.addFunds(100.0);
        assert user.getAvailableFunds() == 200.0 : "Available Funds should equal 200.0";
    }

    @Test
    void subtractFunds() {
        user.subtractFunds(100.0);
        assert user.getAvailableFunds() == 0.0 : "Available Funds should equal 0.0";
    }

}
