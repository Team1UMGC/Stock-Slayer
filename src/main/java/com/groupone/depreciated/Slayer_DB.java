package com.groupone.depreciated;

import java.sql.*;
import java.util.ArrayList;

import com.groupone.model.Stock;
import com.groupone.model.User;

public class Slayer_DB {
    private static String dbConnectionPath = "jdbc:sqlite:slayer.db";

    public static void checkDatabaseExists() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbConnectionPath);
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("The database is connected to: " + meta.getDatabaseProductName());
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    // ...

    public static ArrayList<Stock> getOwnedStocks(User user) throws Exception {
        checkDatabaseExists();
        ArrayList<Stock> ownedStocks = new ArrayList<>();

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbConnectionPath);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!userExists(user)) {
                throw new Exception("User not found!");
            }

            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT * FROM stock WHERE owner_id='%d'",
                    user.getId()));
            while (rs.next()) {
                int stockID = rs.getInt("stock_id");
                int ownerID = rs.getInt("owner_id");
                String symbol = rs.getString("symbol");
                double volume = rs.getDouble("volume");
                double value = rs.getDouble("value");

                Stock stock = new Stock(stockID, ownerID, symbol, volume, value);
                ownedStocks.add(stock);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return ownedStocks;
    }

    public static void updateOwnedStock(User user, Stock stock) throws Exception {
        checkDatabaseExists();

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbConnectionPath);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!userExists(user)) {
                throw new Exception("User not found!");
            }

            // Check if the user already owns this stock
            ArrayList<Stock> ownedStocks = getOwnedStocks(user);
            boolean stockExists = false;
            for (Stock ownedStock : ownedStocks) {
                if (ownedStock.getSymbol().equals(stock.getSymbol())) {
                    stockExists = true;
                    break;
                }
            }

            if (stockExists) {
                // Update the volume and value of the existing stock
                statement.executeUpdate(String.format(
                        "UPDATE stock SET volume=%1$f, value=%2$f WHERE owner_id=%3$d AND symbol='%4$s'",
                        stock.getVolume(),
                        stock.getValue(),
                        user.getId(),
                        stock.getSymbol()
                ));
            } else {
                // Insert a new stock record for the user
                statement.executeUpdate(String.format(
                        "INSERT INTO stock (owner_id, symbol, volume, value) VALUES(%1$d, '%2$s', %3$f, %4$f)",
                        user.getId(),
                        stock.getSymbol(),
                        stock.getVolume(),
                        stock.getValue()
                ));
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void deleteOwnedStock(User user, String symbol) throws Exception {
        checkDatabaseExists();

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbConnectionPath);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!userExists(user)) {
                throw new Exception("User not found!");
            }

            statement.executeUpdate(String.format(
                    "DELETE FROM stock WHERE owner_id=%1$d AND symbol='%2$s'",
                    user.getId(),
                    symbol
            ));

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static boolean userExists(User user) {
        return false;
    }

    // ...
}
