package com.groupone.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(){
        super("User Already Exists in Database");
    }
}
