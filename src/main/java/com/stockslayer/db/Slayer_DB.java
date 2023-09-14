package com.stockslayer.db;

import java.sql.*;
import java.io.File;
import java.nio.file.*;

public class Slayer_DB {
	static Path dbPackagePath = Paths.get("src/main/db");
	static File dbWorkingFile = new File(String.format(
			"%s/test.db", dbPackagePath.toAbsolutePath().toString())
		);
	static boolean tempDebugOverride = true;
	
	private static void printDebugInfo() {
		System.out.println("Current Working Directory: " + dbPackagePath.toAbsolutePath().toString());
		System.out.println(String.format("File Selected: %1$s \t Does File Exist: %2$s", 
				dbWorkingFile.getAbsolutePath(), 
				dbWorkingFile.exists())
			);
	}
	
	
	public class User {
		int id;
		String email;
		String pass;
		OwnedStock stock;
		
		private class OwnedStock {
			int id;
			int owner_id;
			String symbol;
		}
	}
	

	private static void create_DB() throws Exception {
		if(dbWorkingFile.exists() && !tempDebugOverride) {
			throw new Exception("Database already exists!");
		}
		
		dbWorkingFile.createNewFile();
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbWorkingFile.getAbsolutePath());
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate("DROP TABLE IF EXISTS person");
			statement.executeUpdate("CREATE TABLE person (id INTEGER, name STRING)");
			statement.executeUpdate("INSERT INTO person VALUES(1, 'leo')");
			statement.executeUpdate("INSERT INTO person VALUES(2, 'yui')");
//			ResultSet rs = statement.executeQuery("SELECT * FROM person");
//			while(rs.next()) {
//				System.out.println("name = " + rs.getString("name"));
//				System.out.println("id = " + rs.getInt("id"));
//			}
		}catch(SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.print(e.getMessage());
			}
		}
	}
	
	public static void main(String[] args){
		printDebugInfo();
		try {
			create_DB();			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbWorkingFile.getAbsolutePath());
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("SELECT * FROM person");
			while(rs.next()) {
				System.out.println("name = " + rs.getString("name"));
				System.out.println("id = " + rs.getInt("id"));
			}
		}catch(SQLException e) {
			System.err.println(e.getMessage());
		}
	}
}
