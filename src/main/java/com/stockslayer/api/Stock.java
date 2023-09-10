package com.stockslayer.api;

import com.crazzyghost.alphavantage.*;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;

import io.github.cdimascio.dotenv.*;

public class Stock {
	private String apiKey 		= null; // In the root dir, make .env file, with format "ALPHA_VANTAGE_API_KEY=YOUR_API_KEY".
	private Config apiConfig 	= null;
	private String symbol 		= null;
	
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
		System.out.println(
			AlphaVantage.api()
			.timeSeries()
			.intraday()
			.forSymbol(this.symbol)
			.interval(interval)
			.outputSize(outputSize)
			.fetchSync()
			.toString()
		);
	}
	
	public String getIntraDayString(Interval interval, OutputSize outputSize) throws configNotDefinedException {
		if(this.apiConfig == null) {
			throw new configNotDefinedException("Configuration Data not Defined!");
		}
		AlphaVantage.api().init(this.apiConfig);
		TimeSeriesResponse response = AlphaVantage.api()
				.timeSeries()
				.intraday()
				.forSymbol(this.symbol)
				.interval(interval)
				.outputSize(outputSize)
				.fetchSync();
		return response.toString();
	}
	
	
}
