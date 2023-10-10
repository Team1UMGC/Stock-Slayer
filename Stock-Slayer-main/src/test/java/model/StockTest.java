package model;

import com.groupone.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class StockTest {

    static Stock defaultStock;

    @BeforeEach
    void setUp() {
        defaultStock = new Stock(1, 1, "IBM", 123.1, 123.1);
    }

    @Test
    void StockConstructorTest() {
        assert defaultStock.getId() == 1;
        assert defaultStock.getOwnerId() == 1;
        assert Objects.equals(defaultStock.getSymbol(), "IBM");
        assert defaultStock.getVolume() == 123.1;
        assert defaultStock.getVolume() == 123.1;

        Stock stock1 = new Stock(1, "IBM", 123.1, 123.1);
        assert stock1.getId() == 0; //Expected to default to 0
        assert stock1.getOwnerId() == 1;
        assert Objects.equals(stock1.getSymbol(), "IBM");
        assert stock1.getVolume() == 123.1;
        assert stock1.getValue() == 123.1;

        Stock stock2 = new Stock("IBM", 123.1, 123.1);
        assert stock2.getId() == 0; //Expected to default to 0
        assert stock2.getOwnerId() == 0; //Expected to default to 0
        assert Objects.equals(stock2.getSymbol(), "IBM");
        assert stock2.getVolume() == 123.1;
        assert stock2.getValue() == 123.1;
    }
}
