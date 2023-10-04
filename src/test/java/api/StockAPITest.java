package api;

import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import com.groupone.StockSlayerApplication;
import com.groupone.api.StockAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.util.AssertionErrors.assertNotNull;

@Disabled
@SpringBootTest(classes = {StockSlayerApplication.class})
public class StockAPITest {

    @Autowired
    StockAPI stockAPI;

    static Interval interval;
    static OutputSize outputSize;
    static String stockSymbol;
    static String incorrectStockSymbol;
    @BeforeEach
    void setUp() {
        interval = Interval.FIFTEEN_MIN;
        outputSize = OutputSize.COMPACT;
        stockSymbol = "IBM";
        incorrectStockSymbol = "asdfghjkl";
    }


    @Test
    void getIntraDayResponseTest() throws Exception {
        TimeSeriesResponse response = stockAPI.getIntraDayResponse(interval, outputSize, stockSymbol);
        assertNotNull("Object is expected to be not null", response);
//        System.out.println(response.getErrorMessage());
//        assert Objects.equals(response.getErrorMessage(), "null");

        TimeSeriesResponse incorrectSymbolResponse = stockAPI.getIntraDayResponse(interval, outputSize, incorrectStockSymbol);
        assert incorrectSymbolResponse.getErrorMessage().contains("Invalid API call.");
    }

    @Test
    void getLastTenDaysTest() throws Exception{
        List<StockUnit> stockUnits = stockAPI.getLastTenDays(stockSymbol);
        assert stockUnits.size() == 10;
    }

//    @Test FIXME This test might not be possible give that we only have 5 responses in one minute as a free user...
//    void getMultipleIntraDayStockUnitsTest(){
//        List<String> stocks = List.of("IBM", "APPL", "BROS");
//        HashMap<String, List<StockUnit>> map = stockAPI.getMultipleIntraDayStockUnits(stocks);
//        assert !map.get("IBM").isEmpty();
//        assert !map.get("APPL").isEmpty();
//        assert !map.get("BROS").isEmpty();
//
//        assert map.get("IBM").get(0).getClose() > 0;
//        assert map.get("APPL").get(0).getClose() > 0;
//        assert map.get("BROS").get(0).getClose() > 0;
//    }
}
