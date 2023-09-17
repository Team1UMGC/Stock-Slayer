package com.stockslayer.db;

import java.sql.*;
import java.util.ArrayList;
import java.io.File;
import java.nio.file.*;

public class Slayer_DB {
	static private String dbFileName = "users.db"; 								// file name for db
	static private Path dbPackagePath = Paths.get("src/main/db");				// relative path for db
	static private File dbWorkingFile = new File(String.format(					// creates File object using a generated absolute path
			"%1$s/%2$s", dbPackagePath.toAbsolutePath().toString(), dbFileName)
		);
	static private String dbConnectionPath = "jdbc:sqlite:" + dbWorkingFile.getAbsolutePath(); // Using File object to 
																							   // create legal path name for db connection
	
	static boolean isDebug = true; // If debug mode is true, when create_DB is called, overwrite the db.
	
	
	/**
	 * Prints Debug information to console
	 */
	private static void printDebugInfo() {
		System.out.println("Current Working Directory: " + dbPackagePath.toAbsolutePath().toString());
		System.out.println(String.format("File Selected: %1$s \t Does File Exist: %2$s", 
				dbWorkingFile.getAbsolutePath(), 
				dbWorkingFile.exists())
			);
	}
	

	/**
	 * Creates a database with class supplied dbConnectionPath string.
	 * Only overwrites the database user table if isDebug is true.
	 * @throws Exception gets thrown if the database already exist and does not need to be created.
	 */
	private static void create_DB() throws Exception {
		if(dbWorkingFile.exists() && !isDebug) {
			throw new Exception("Database already exists!");
		}
		
		dbWorkingFile.createNewFile();
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			//Creates User table
			statement.executeUpdate("DROP TABLE IF EXISTS user");
			statement.executeUpdate(
					  "CREATE TABLE user"
					+ "(user_id INTEGER PRIMARY KEY,"
					+ "email STRING NOT NULL,"
					+ "password STRING NOT NULL)"
					);
			
			//Creates stock table
			statement.executeUpdate("DROP TABLE IF EXISTS stock");
			statement.executeUpdate(
					  "CREATE TABLE stock"
					+ "(stock_id INTEGER PRIMARY KEY,"
					+ "owner_id INTEGER,"
					+ "symbol STRING NOT NULL,"
					+ "volume DOUBLE NOT NULL,"
					+ "value DOUBLE NOT NULL,"
					+ "FOREIGN KEY (owner_id) REFERENCES user(user_id))"
					);
			
		}catch(SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Inserts new user information into the database
	 * @param user object that contains user data such as email and password
	 * @throws Exception If the database does not exist or if user already exists in database
	 */
	private static User insertNewUser(User user) throws Exception {
		checkDatabaseExists();
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			if(userExists(user)) throw new Exception("User already exists in database!");
			
			statement.executeUpdate(String.format("INSERT INTO user (email, password) VALUES('%1$s', '%2$s')", 
					user.getEmail(),
					user.getPassword()
					)
				);
			ResultSet rs = statement.executeQuery(String.format("SELECT * FROM user WHERE email='%s'", user.getEmail()));
			user.setID(rs.getInt("user_id"));
			
		} catch(SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		
		return user;
	}
	
	/**
	 * Deletes user from the database
	 * @param user user that is to be removed from database. Uses the email of the user.
	 * @throws Exception If database does not exist or the user cannot be found in the database.
	 */
	private static void deleteUser(User user) throws Exception {
		checkDatabaseExists();
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			if(!userExists(user)) throw new Exception("User not found!");
			
			statement.executeUpdate(String.format("DELETE FROM user WHERE email='%s'", user.getEmail()));
			
		} catch(SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	private static User getUser(String email) throws Exception{
		checkDatabaseExists();
		
		User acquiredUser = new User();
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			if(!userExists(email)) throw new Exception("User not found!");
			
			// Get user based information
			ResultSet userRs = statement.executeQuery(String.format("SELECT * FROM user WHERE email='%s'", email));
			int id = userRs.getInt("user_id");
			String password = userRs.getString("password");
			
			// Get stock based information from user
			ArrayList<Stock> stocks = new ArrayList<Stock>();
			ResultSet stockRs = statement.executeQuery(String.format("SELECT * FROM stock WHERE owner_id='%d'", id));
			while(stockRs.next()) {
				int stockID = stockRs.getInt("stock_id");
				int ownerID = stockRs.getInt("owner_id");
				String symbol = stockRs.getString("symbol");
				double volume = stockRs.getDouble("volume");
				double value = stockRs.getDouble("value");
				
				Stock stock = new Stock(stockID, ownerID, symbol, volume, value);
				stocks.add(stock);
			}
			
			// update created user object with acquired information
			acquiredUser.setID(id);
			acquiredUser.setEmail(email);
			acquiredUser.setPassword(password);
			acquiredUser.setStocks(stocks);
			
			
		} catch(SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		
		return acquiredUser;
	}
	
	
	/**
	 * Updates a user's email in the database
	 * @param user The user that is being updated
	 * @param newEmail The new email string that replaces the old string
	 * @return Returns a new updated user object. This should replace the parameter user.
	 * @throws Exception If the database is not found, or the user was not found in the database.
	 */
	private static User updateEmail(User user, String newEmail) throws Exception {
		checkDatabaseExists();
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			if(!userExists(user)) throw new Exception("User not found!");
			
			statement.executeUpdate(String.format("UPDATE user SET email='%1$s' WHERE email='%2$s'", 
					newEmail, 
					user.getEmail()));
			
			user.setEmail(newEmail);
			
		} catch(SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		
		return user;
	}
	
	
	/**
	 * Updates the password of a user in the database.
	 * @param user The user that is having the password updated. Uses email column to find the user.
	 * @param newPassword String of the password the user wishes to change to
	 * @return Returns a user object so that the called object can be updated in memory for future updates.
	 * @throws Exception Throws if database cannot be found or if user is not found.
	 */
	private static User updatePassword(User user, String newPassword) throws Exception {
		checkDatabaseExists();
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			if(!userExists(user)) throw new Exception("User not found!");
			
			statement.executeUpdate(String.format("UPDATE user SET password='%1$s' WHERE email='%2$s'", 
					newPassword, 
					user.getEmail()));
			user.setPassword(newPassword);
			
		} catch(SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		
		return user;
	}
	
	
	/**
	 * Adds stock to the stock table with the owner_id dependent on the user parameter
	 * @param user User that is buying new stock
	 * @param stock The Stock information to be stored
	 * @return returns a user object that is the updated version of the user parameter.
	 * @throws Exception Thrown if database does not exist or the user in the user table is not found.
	 */
	private static User addNewStock(User user, Stock stock) throws Exception {
		checkDatabaseExists();
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			if(!userExists(user)) throw new Exception("User not found!");
			
			statement.executeUpdate(String.format(
					"INSERT INTO stock (owner_id, symbol, volume, value) VALUES(%1$d, '%2$s', %3$f, %4$f)", 
					user.getID(),
					stock.getSymbol(),
					stock.getVolume(),
					stock.getValue())
					);
			user.addStock(stock);
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		
		return user;
	}
	
	private static User deleteStock(User user, Stock stock) throws Exception { // TODO, needs some helper methods to read already owned stock from the stock table
		return user;
	}
	
	
	/**
	 * Checks if the user exists in the database by matching user.email in the users table, email column.
	 * @param user user object that is being used as a reference in the database to see if the email exists.
	 * @return If user exists, true, if no user is found, then false
	 * @throws Exception Thrown if database cannot be found.
	 */
	public static Boolean userExists(User user) throws Exception {
		checkDatabaseExists();
		Boolean userExists = false;
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			ResultSet rs = statement.executeQuery(String.format("SELECT * FROM user WHERE email LIKE '%s'", user.getEmail()));
			if(rs.next()) userExists = true;
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		
		return userExists;
	}
	
	
	/**
	 * Checks if the user exists in the database using a provided email sting
	 * @param email Email of the user that is being looked for
	 * @return If user exists, true, if no user is found, then false
	 * @throws Exception Thrown if database cannot be found.
	 */
	public static Boolean userExists(String email) throws Exception {
		checkDatabaseExists();
		Boolean userExists = false;
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			ResultSet rs = statement.executeQuery(String.format("SELECT * FROM user WHERE email LIKE '%s'", email));
			if(rs.next()) userExists = true;
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		
		return userExists;
	}
	
	/**
	 * Prints all entries from the users table in the users.db
	 * @throws Exception throws if the database cannot be found.
	 */
	private static void printUserTable() throws Exception {
		checkDatabaseExists();
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("SELECT * FROM user");
			System.out.println("rowid\t\temail\t\tpassword\n");
			while(rs.next()) {
				System.out.println(String.format("%1$s\t\t%2$s\t\t%3$s", 
						rs.getInt("user_id"),
						rs.getString("email"),
						rs.getString("password")
						)
					);
			}
		}catch(SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Prints the stock table from the database to the console.
	 * @throws Exception If the database does not exist
	 */
	private static void printStockTable() throws Exception {
		checkDatabaseExists();
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbConnectionPath);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("SELECT * FROM stock");
			while(rs.next()) {
				System.out.println(String.format("%1$d\t%2$d\t%3$s\t%4$f\t%5$f", 
						rs.getInt("stock_id"),
						rs.getInt("owner_id"),
						rs.getString("symbol"),
						rs.getDouble("volume"),
						rs.getDouble("value")
						)
					);
			}
		}catch(SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Checks if the database in the in expected spot in order to update the file.
	 * Throws exception if there is no database found, does nothing if one is found.
	 * @throws Exception gets thrown if no database is found.
	 */
	private static void checkDatabaseExists() throws Exception {
		if(!dbWorkingFile.exists()) {
			throw new Exception("No Database Found!");
		}
	}
	
	public static void main(String[] args){
		printDebugInfo();
		User testUser = new User("test@test.com", "p@ssW0rd");
		User user = new User("CStoner00@StockSlayer.com", "3t7d@vy");
		
		Stock testStock = new Stock("DOW", 500, 36);
		Stock stock = new Stock("IBM", 300, 24);
		try {
			create_DB();
			testUser = insertNewUser(testUser);
			user = insertNewUser(user);
			
			testUser = addNewStock(testUser, testStock);
			user = addNewStock(user, stock);
			user = addNewStock(user, testStock);
			
			printStockTable();
			printUserTable();
			
			User dbUser = getUser(user.getEmail());
			dbUser.printData();
			
			
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
