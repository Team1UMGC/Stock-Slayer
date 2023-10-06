package com.groupone.exception;

public class StockNotFoundException extends Exception {
    public StockNotFoundException(){
        super("No Stock with Given ID was Found");
    }
}
