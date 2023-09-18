package com.stockslayer.db;

public class Stock {
	int stockID;
	int ownerID;
	String symbol;
	double volume;
	double value;
	
	public Stock(int stockID, int ownerID, String symbol, double volume, double value) {
		this.stockID = stockID;
		this.ownerID = ownerID;
		this.symbol = symbol;
		this.volume = volume;
		this.value = value;
		
		
	}
	
	public Stock(String symbol, double volume, double value) {
		this.symbol = symbol;
		this.volume = volume;
		this.value = value;
	}
	
	public void printStockData() {
		System.out.println("stockID : " + this.stockID);
		System.out.println("ownerID : " + this.ownerID);
		System.out.println("symbol : " + this.symbol);
		System.out.println("volume : " + this.volume);
		System.out.println("value : " + this.value);
	}
	
	public int getStockID() {
		return this.stockID;
	}
	
	public void setStockID(int stockID) {
		this.stockID = stockID;
	}
	
	public int getOwnerID() {
		return this.ownerID;
	}
	
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
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
