package com.groupone.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(){
        super("User Not Found in Database");
    }

    public UserNotFoundException(String message){
        super(message);
    }
}
