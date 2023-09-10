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
    		String IBMStockStr = IBM.getIntraDayString(Interval.FIVE_MIN, OutputSize.COMPACT);
    		System.out.println(IBMStockStr);
    	} catch (configNotDefinedException e) {
    		e.printStackTrace();
    	}

    }

}