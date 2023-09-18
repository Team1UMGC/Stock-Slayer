package com.stockslayer.api;

import java.util.List;

import com.crazzyghost.alphavantage.*;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;

import com.google.gson.*;

import io.github.cdimascio.dotenv.*;

public class API_Stock {
	private static String apiKey 		= null; // In the root dir, make .env file, with format "ALPHA_VANTAGE_API_KEY=YOUR_API_KEY".
	private static Config apiConfig 	= null;
	
	private class StockInfo {
		
	}
	
	public API_Stock() {
		this.apiKey = Dotenv.load().get("ALPHA_VANTAGE_API_KEY");
		this.apiConfig = Config.builder().key(apiKey).timeOut(10).build();
	}
	
	public void printIntraDay(Interval interval, OutputSize outputSize, String symbol) throws configNotDefinedException {
		if(this.apiConfig == null) {
			throw new configNotDefinedException("Configuration Data not Defined!");
		}
		
		AlphaVantage.api().init(this.apiConfig);
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
	
	public TimeSeriesResponse getIntraDayResponse(Interval interval, OutputSize outputSize, String symbol) throws configNotDefinedException {
		if(this.apiConfig == null) {
			throw new configNotDefinedException("Configuration Data not Defined!");
		}
		AlphaVantage.api().init(this.apiConfig);
		TimeSeriesResponse response = AlphaVantage.api()
				.timeSeries()
				.intraday()
				.forSymbol(symbol)
				.interval(interval)
				.outputSize(outputSize)
				.fetchSync();
		
		return response;
	}
	
	public static List<StockUnit> getLastTenDays(String symbol) throws configNotDefinedException {
		if(apiConfig == null) throw new configNotDefinedException("Configuration Data not Defined!");
		
		AlphaVantage.api().init(apiConfig);
		TimeSeriesResponse response = AlphaVantage.api()
				.timeSeries()
				.daily()
				.forSymbol(symbol)
				.fetchSync();
		return response.getStockUnits().subList(0, 9);
	}
	
	public static void main(String[] args) {
		API_Stock apiRequester = new API_Stock();
		
		try {
			List<StockUnit> stockUnits = getLastTenDays("IBM");
			System.out.println("IBM Last 10 Days");
			stockUnits.forEach(e->{
				System.out.println(e.getDate());
				System.out.println("OPEN : " + e.getOpen());
				System.out.println("HIGH : " + e.getHigh());
				System.out.println("LOW : " + e.getLow());
				System.out.println("CLOSE : " + e.getClose());
				System.out.println("VOLUME : " + e.getVolume());
				System.out.println("*******************");
			});
			
			
			
		} catch (configNotDefinedException e) {
			System.err.println(e.getMessage());
		}
	}
	
	
}
