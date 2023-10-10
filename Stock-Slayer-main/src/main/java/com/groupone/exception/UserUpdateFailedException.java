package com.groupone.exception;

public class UserUpdateFailedException extends Exception {
    public UserUpdateFailedException(){
        super("Attempted Update of a User has Failed");
    }
}
