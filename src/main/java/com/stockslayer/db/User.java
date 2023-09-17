package com.stockslayer.db;

import java.util.ArrayList;

public class User {
	int id;
	String email;
	String password;
	ArrayList<Stock> ownedStocks = new ArrayList<Stock>();
	
	User() {
		this.id = 0;
		this.email = "";
		this.password = "";
	}
	
	User(String email, String password){
		this.email = email;
		this.password = password;
	}
	
	public void addStock(String symbol, int volume, int value) {
		Stock newStock = new Stock(symbol, volume, value);
		this.ownedStocks.add(newStock);
	}
	
	public void addStock(Stock stock) {
		this.ownedStocks.add(stock);
	}
	
	public void updateUser(String email, String password) {
		setEmail(email);
		setPassword(password);
	}
	
	public void printData() {
		System.out.println("id : " + this.id);
		System.out.println("email : " + this.email);
		System.out.println("password : " + this.password);
		System.out.println("num of stocks : " + this.ownedStocks.size());
		ownedStocks.forEach(e->{
			System.out.println();
			e.printStockData();
			});
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public ArrayList<Stock> getStocks(){
		return this.ownedStocks;
	}
	
	public void setStocks(ArrayList<Stock> stocks) {
		this.ownedStocks = stocks;
	}
}