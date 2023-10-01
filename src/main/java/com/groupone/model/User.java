package com.groupone.model;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Abstracts and stores information about a user such as ID, email, password, and owned stocks
 */

public class User {
	private int id;													// User ID, this must be unique to each user object
	private String email;											// String containing the email of the registered user
	private String password;										// String of the password of the user. Should be encrypted, eventually...
	private Boolean isLocked = false;								// Used to lock account and deny login.
	private double availableFunds = 0.0;							// Funds for purchasing stock
	private ArrayList<Stock> ownedStocks = new ArrayList<>();		// ArrayList of Stock objects, where each Stock object contains information about an owned stock
	
	/**
	 * Creates empty object instance
	 */
	User() {
		this.id = 0;
		this.email = "";
		this.password = "";
	}

	/**
	 * Creates instance of User object with id, email, and password
	 * @param id User ID, this must be unique to each user object
	 * @param email String containing the email of the registered user
	 * @param password String password of the user
	 */
	public User(int id, String email, String password) {
		this.id = id;
		this.email = email;
		this.password = password;
	}

	/**
	 * Creates instance of User object with id, email, password, and isLocked
	 * @param id User ID, this must be unique to each user object
	 * @param email String containing the email of the registered user
	 * @param password String password of the user
	 * @param isLocked Boolean, Used to lock account and deny login
	 */
	public User(int id, String email, String password, Boolean isLocked) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.isLocked = isLocked;
	}

	/**
	 * Creates instance of User object with id, email, password, isLocked, availableFunds
	 * @param id User ID, this must be unique to each user object
	 * @param email String containing the email of the registered user
	 * @param password String password of the user
	 * @param isLocked boolean, Used to lock account and deny login
	 * @param availableFunds double, funds for purchasing stock
	 */
	public User(int id, String email, String password, Boolean isLocked, double availableFunds) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.isLocked = isLocked;
		this.availableFunds = availableFunds;
	}

	/**
	 * Creates instance of User object with id, email, password, isLocked, availableFunds, ownedStocks
	 * @param id User ID, this must be unique to each user object
	 * @param email String containing the email of the registered user
	 * @param password String password of the user
	 * @param isLocked boolean, Used to lock account and deny login
	 * @param availableFunds double, funds for purchasing stock
	 * @param ownedStocks ArrayList of Stock objects, where each Stock object contains information about an owned stock
	 */
	public User(int id, String email, String password, Boolean isLocked, double availableFunds, ArrayList<Stock> ownedStocks) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.isLocked = isLocked;
		this.availableFunds = availableFunds;
		this.ownedStocks = ownedStocks;
	}

	/**
	 * Creates object with values email and password initialized
	 * @param email String of the registered user's email
	 * @param password String of the registered user's password
	 */
	public User(String email, String password){
		this.email = email;
		this.password = password;
	}
	
	/**
	 * Adds stock to array list using the bare values
	 * @param symbol String, symbol of the stock
	 * @param volume double, volume of the stock
	 * @param value double, value of the stock
	 */
	public void addStock(String symbol, double volume, double value) {
		Stock newStock = new Stock(symbol, volume, value);
		this.ownedStocks.add(newStock);
	}
	
	/**
	 * Adds stock directly using a stock object
	 * @param stock Stock object abstracting data about a stock
	 */
	public void addStock(Stock stock) {
		this.ownedStocks.add(stock);
	}
	
	/**
	 * Adds multiple stocks from an array list to the ownedStocks array list
	 * @param stocks ArrayList object that contains Stock data
	 */
	public void addStock(ArrayList<Stock> stocks) {
		this.ownedStocks.addAll(stocks);
	}
	
	/** TODO Needs to check if the old email or password is being used to update, if so, throw an expection
	 * Updates a user's email and password in one method
	 * @param email new email
	 * @param password new password
	 */
	public void updateUser(String email, String password) {
		setEmail(email);
		setPassword(password);
	}
	
	/**
	 * Gets an array list of stock objects defined by the parameter symbol
	 * @param symbol String, symbol of stock, such as "APPL" or "IMB"
	 * @return Returns ArrayList<Stock> object filled with stocks of the same symbol entered the parameters
	 */
	public ArrayList<Stock> getStocksBySymbol(String symbol){
		ArrayList<Stock> symbolStock = new ArrayList<>();
		this.ownedStocks.forEach(e -> {
			if(Objects.equals(e.symbol, symbol)) symbolStock.add(e);
		});
		return symbolStock;
	}
	
	/** TODO Should be checking if the stock belongs to the user, if not, throw an exception
	 * Removes a stock from the kept in ownedStocks where the stockID matches that stock
	 * @param stockID integer, stock_id that is going to be removed from the list
	 */
	public void removeStock(int stockID) {
		boolean stockFound = false;
		int removalIndex = 0;
		for(int i = 0; i < ownedStocks.size(); i++) {
			Stock stock = ownedStocks.get(i);
			if(stock.getId() == stockID) {
				stockFound = true;
				removalIndex = i;
				break;
			}
		}
		
		if(stockFound) ownedStocks.remove(removalIndex);
	}
	
	/**
	 * Returns the id of the user
	 * @return integer, returns id of user
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Sets the id of the user
	 * @param id integer to be set as the new user id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns String of the email related to the user's account
	 * @return String, email of the user
	 */
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * Set the email of the user
	 * @param email String to be set as the new user email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Returns String of the password related to the user's account
	 * @return String, password of the user
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Set the password of the user
	 * @param password String to be set as the new user password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Returns all stocks owned by the user
	 * @return ArrayList<Stock> all stocks owned by the user
	 */
	public ArrayList<Stock> getStocks(){
		return this.ownedStocks;
	}
	
	/**
	 * Set all stocks owned by the user. Will override the stocks array list already present.
	 * @param stocks ArrayList<Stock> of stock objects that is owned by the user. 
	 */
	public void setStocks(ArrayList<Stock> stocks) {
		this.ownedStocks = stocks;
	}

	/**
	 * Returns a boolean that indicates if the user's account is
	 * @return boolean, Used to lock account and deny login
	 */
	public boolean getIsLocked() {
		return isLocked;
	}

	/**
	 * Sets the locked status of a user's account
	 * @param locked boolean, sets the locked status to the input value
	 */
	public void setIsLocked(boolean locked) {
		isLocked = locked;
	}

	/**
	 * Toggles the isLocked field between true and false
	 */
	public void toggleLock(){
        this.setIsLocked(!getIsLocked());
	}

	/**
	 * Returns the double value of what funds the user has to spend
	 * @return double, funds for purchasing stock
	 */
	public double getAvailableFunds() {
		return this.availableFunds;
	}

	/**
	 * Sets the amount of funds available to the user for purchasing stocks
	 * @param availableFunds double, this value passed is set to the availableFunds field
	 */
	public void setAvailableFunds(double availableFunds) {
		this.availableFunds = availableFunds;
	}

	/**
	 * Adds the passed value to the availableFunds field of a User object
	 * @param value The value that will be added to the availableFunds
	 */
	public void addFunds(double value){
		this.availableFunds += value;
	}

	/**
	 * Subtracts the passed value to the availableFunds field of a User Object
	 * @param value The value that will be subtracted to the availableFunds
	 */
	public void subtractFunds(double value){
		this.availableFunds -= value;
	}

	/**
	 * returns a string that contains information about the referenced user object
	 * @return String, info about the user object
	 */
	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				", isLocked=" + isLocked +
				", availableFunds=" + availableFunds +
				", ownedStocks=" + ownedStocks +
				'}';
	}
}