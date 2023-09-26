package com.groupone.dao;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockAPI {
    private final Config API_CONFIG;

    public StockAPI(@Value("${ALPHA_VANTAGE_API_KEY}") String apiKey){
        API_CONFIG = Config.builder().key(apiKey).timeOut(10).build();
    }

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

    public List<StockUnit> getLastTenDays(String symbol) throws Exception {
        if (API_CONFIG == null) throw new Exception("Configuration Data not Defined!");

        AlphaVantage.api().init(API_CONFIG);
        return AlphaVantage.api()
                .timeSeries()
                .daily()
                .forSymbol(symbol)
                .fetchSync()
                .getStockUnits()
                .subList(0, 9);
    }

}
