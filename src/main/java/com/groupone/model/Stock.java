package com.groupone.model;


/**
 * Abstracts and stores information about a stock that a user has purchased such as
 * the unique stock ID (transaction), the owner ID of the one who purchased the stock,
 * the symbol, the volume, and the value.
 */
public class Stock {
	int stockID;		// Unique ID integer given to every stock transaction
	int ownerID;		// Unique ID integer linked to the user id, the one who purchased the stock
	String symbol;		// String symbol of the stock, such as "IBM" or "APPL"
	double volume;		// Double volume, or number of shares purchased
	double value;		// Double value, value of each share that was purchased
	
	/**
	 * Creates Stock object with all data filled out
	 * @param stockID Unique ID integer given to every stock transaction
	 * @param ownerID Unique ID integer linked to the user id, the one who purchased the stock
	 * @param symbol String symbol of the stock, such as "IBM" or "APPL"
	 * @param volume Double volume, or number of shares purchased
	 * @param value Double value, value of each share that was purchased
	 */
	public Stock(int stockID, int ownerID, String symbol, double volume, double value) {
		this.stockID = stockID;
		this.ownerID = ownerID;
		this.symbol = symbol;
		this.volume = volume;
		this.value = value;
	}
	
	/**
	 * Creates a partly made Stock object with only symbol, volume, and value filled out. No associated user or stock id.
	 * @param symbol String symbol of the stock, such as "IBM" or "APPL"
	 * @param volume Double volume, or number of shares purchased
	 * @param value Double value, value of each share that was purchased
	 */
	public Stock(String symbol, double volume, double value) {
		this.symbol = symbol;
		this.volume = volume;
		this.value = value;
	}
	
	/**
	 * Prints ALL the data in the object
	 */
	public void printStockData() {
		System.out.println("stockID : " + this.stockID);
		System.out.println("ownerID : " + this.ownerID);
		System.out.println("symbol : " + this.symbol);
		System.out.println("volume : " + this.volume);
		System.out.println("value : " + this.value);
	}
	
	/**
	 * Returns the integer stockID attribute
	 * @return integer stockID
	 */
	public int getStockID() {
		return this.stockID;
	}
	
	/**
	 * Sets the integer stockID attribute
	 * @param stockID Integer stockID
	 */
	public void setStockID(int stockID) {
		this.stockID = stockID;
	}
	
	/**
	 * Returns the integer ownerID attribute
	 * @return integer ownerID
	 */
	public int getOwnerID() {
		return this.ownerID;
	}
	
	/**
	 * Sets the integer ownerID attribute
	 * @param ownerID Integer
	 */
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}
	
	/**
	 * Returns the String symbol attribute
	 * @return String symbol
	 */
	public String getSymbol(){
		return this.symbol;
	}
	
	/**
	 * Returns the double volume attribute
	 * @return Double volume
	 */
	public double getVolume() {
		return this.volume;
	}
	
	/**
	 * Returns the double value attribute
	 * @return Double value
	 */
	public double getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "Stock{" +
				"stockID=" + stockID +
				", ownerID=" + ownerID +
				", symbol='" + symbol + '\'' +
				", volume=" + volume +
				", value=" + value +
				'}';
	}
}