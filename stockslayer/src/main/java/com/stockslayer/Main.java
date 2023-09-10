package com.stockslayer;

import com.crazzyghost.alphavantage.*;
import com.crazzyghost.alphavantage.parameters.*;
import com.crazzyghost.alphavantage.timeseries.response.*;

import io.github.cdimascio.dotenv.*;

// import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() {
        // new StockSlayer();
        // }
        // });

        // Get and Config API Key
        String apiKey = Dotenv.load().get("ALPHA_VANTAGE_API_KEY");

        Config cfg = Config.builder().key(apiKey).timeOut(10).build();
        AlphaVantage.api().init(cfg);

        AlphaVantage.api()
                .timeSeries()
                .intraday()
                .forSymbol("IBM")
                .interval(Interval.FIVE_MIN)
                .outputSize(OutputSize.FULL)
                .onSuccess(e -> handleSuccess((TimeSeriesResponse) e))
                .onFailure(e -> handleFailure(e))
                .fetch();

    }

    public static void handleSuccess(TimeSeriesResponse response) {
        System.out.println(response.getMetaData().toString());
        System.exit(0);
    }

    public static void handleFailure(AlphaVantageException response) {
        System.out.println(response.getMessage());
        System.exit(1);
    }

}