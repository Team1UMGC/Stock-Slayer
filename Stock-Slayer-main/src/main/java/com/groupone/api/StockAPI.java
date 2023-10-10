package com.groupone.api;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class StockAPI {
    private final Config API_CONFIG;

    /**
     * Constructor that sets up the configuration data for the API wrapper
     * @param apiKey String, alpha vantage API key
     */
    public StockAPI(@Value("${ALPHA_VANTAGE_API_KEY}") String apiKey){
        API_CONFIG = Config.builder().key(apiKey).timeOut(10).build();
    }

    /**
     * Prints intra-day info about a single stock. Mainly used for debugging.
     * @param interval Interval, interval object, expected to be something that works with IntraDay call like Interval.FIFTEEN_MIN
     * @param outputSize OutputSize, outputSize object to tell the request what kind of output to produce.
     * @param symbol String, symbol of the stock that is attempting to be requested
     * @throws Exception Thrown if config data for the alpha vantage api wrapper hasn't been defined
     */
    public void printIntraDay(Interval interval, OutputSize outputSize, String symbol) throws Exception {
        if (API_CONFIG == null) {
            throw new Exception("Configuration Data not Defined!");
        }

        AlphaVantage.api().init(API_CONFIG);
        System.out.println(
            AlphaVantage.api()
                .timeSeries()
                .intraday()
                .forSymbol(symbol)
                .interval(interval)
                .outputSize(outputSize)
                .fetchSync()
                .toString()
        );
    }

    /**
     * Returns intra-day info about a single stock as a TimeSeriesResponse object
     * @param interval Interval, interval object, expected to be something that works with IntraDay call like Interval.FIFTEEN_MIN
     * @param outputSize OutputSize, outputSize object to tell the request what kind of output to produce.
     * @param symbol String, symbol of the stock that is attempting to be requested
     * @return TimeSeriesResponse, contains data different object types that contain information about the stock being requested
     * @throws Exception Thrown if config data for the alpha vantage api wrapper hasn't been defined
     */
    public TimeSeriesResponse getIntraDayResponse(Interval interval, OutputSize outputSize, String symbol) throws Exception {
        if (API_CONFIG == null) {
            throw new Exception("Configuration Data not Defined!");
        }
        AlphaVantage.api().init(API_CONFIG);
        return AlphaVantage.api()
            .timeSeries()
            .intraday()
            .forSymbol(symbol)
            .interval(interval)
            .outputSize(outputSize)
            .fetchSync();
    }

    /**
     *
     * @param symbol
     * @return
     * @throws Exception
     */
    public double getCurrentPrice(String symbol) throws Exception{
        TimeSeriesResponse timeSeriesResponse = getIntraDayResponse(Interval.FIFTEEN_MIN, OutputSize.COMPACT, symbol);
        if(timeSeriesResponse.getStockUnits().isEmpty()) {
            throw new Exception(timeSeriesResponse.getErrorMessage());
        }
        return timeSeriesResponse.getStockUnits().get(0).getClose();
    }

    /**
     *
     * @param symbol
     * @return
     * @throws Exception
     */
    public List<StockUnit> getLastTenDays(String symbol) throws Exception {
        if (API_CONFIG == null) throw new Exception("Configuration Data not Defined!");

        AlphaVantage.api().init(API_CONFIG);
        return AlphaVantage.api()
                .timeSeries()
                .daily()
                .forSymbol(symbol)
                .fetchSync()
                .getStockUnits()
                .subList(0, 10);
    }

//    public HashMap<String, List<StockUnit>> getMultipleIntraDayStockUnits(List<String> symbols){ FIXME this method might not be possible given that we only have 5 responses in one minute as a free user...
//        HashMap<String, List<StockUnit>> stockUnitMap = new HashMap<>();
//
//        symbols.forEach(symbol -> {
//            try{
//                List<StockUnit> stockUnits = getIntraDayResponse(Interval.FIFTEEN_MIN, OutputSize.COMPACT, symbol).getStockUnits();
//                stockUnitMap.put(symbol, stockUnits);
//            }catch(Exception e){
//                System.err.println(e.getMessage());
//            }
//        });
//
//        return stockUnitMap;
//    }

}
