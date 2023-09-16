package com.stockslayer.db;

public class Stock {
	String symbol;
	double volume;
	double value;
	
	public Stock(String symbol, double volume, double value) {
		this.symbol = symbol;
		this.volume = volume;
		this.value = value;
	}
	
	public String getSymbol(){
		return this.symbol;
	}
	
	public double getVolume() {
		return this.volume;
	}
	
	public double getValue() {
		return this.value;
	}
}
