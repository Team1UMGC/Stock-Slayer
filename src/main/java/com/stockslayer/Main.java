package com.stockslayer;

import com.stockslayer.api.*;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;

// import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() {
        // new StockSlayer();
        // }
        // });

        // Get and Configure API Key
    	Stock IBM = new Stock("IBM");
    	try {
			IBM.printIntraDay(Interval.FIVE_MIN, OutputSize.COMPACT);
		} catch (configNotDefinedException e) {
			e.printStackTrace();
		}

    }

//    public static void handleSuccess(TimeSeriesResponse response) {
//        System.out.println(response.getMetaData().toString());
//        System.exit(0);
//    }
//
//    public static void handleFailure(AlphaVantageException response) {
//        System.out.println(response.getMessage());
//        System.exit(1);
//    }

}