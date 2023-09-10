package com.stockslayer.api;

import com.crazzyghost.alphavantage.*;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
//import com.crazzyghost.alphavantage.timeseries.response.StockUnit;

import io.github.cdimascio.dotenv.*;

public class Stock {
	private String apiKey 		= null;
	private Config apiConfig 	= null;
	private String symbol 		= null;
	
	public Stock(){
		this.apiKey = Dotenv.load().get("ALPHA_VANTAGE_API_KEY");
		this.apiConfig = Config.builder().key(apiKey).timeOut(10).build();
	}
	
	public Stock(String symbol) {
		this.apiKey = Dotenv.load().get("ALPHA_VANTAGE_API_KEY");
		this.apiConfig = Config.builder().key(apiKey).timeOut(10).build();
		this.symbol = symbol;
	}
	
	public void printIntraDay(Interval interval, OutputSize outputSize) throws configNotDefinedException {
		if(this.apiConfig == null) {
			throw new configNotDefinedException("Configuration Data not Defined!");
		}
		
		AlphaVantage.api().init(this.apiConfig);
		AlphaVantage.api()
		.timeSeries()
		.intraday()
		.forSymbol(this.symbol)
		.interval(interval)
		.outputSize(outputSize)
		.onSuccess(e-> {
			TimeSeriesResponse response = (TimeSeriesResponse) e;
			System.out.println(response.toString());
		})
		.onFailure(e->handleFailure(e))
		.fetch();
	}
	
	private void handleFailure(AlphaVantageException response) {
      System.out.println(response.getMessage());
  }
	
	
}
