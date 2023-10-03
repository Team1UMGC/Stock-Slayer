package com.groupone.service;

import com.groupone.api.DatabaseAPI;
import com.groupone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * This service is used to log in, register, and authenticate logging in as well as hold what user is
 * current logged in to the site. Any user information about a session should be here.
 */
@Service
public class UserService {

    @Autowired
    DatabaseService databaseService;

    User loggedAs = null;

    /**
     * Authenticates that the user has a registered account in the database
     * @param email Checks that this email passed is in the database
     * @param password Checks that the email found contains this password
     * @return boolean, returns true if the user was found and authenticated with the correct password, returns false otherwise.
     */
    public boolean authenticate(String email, String password) { // TODO, check if the account is locked
        boolean matchFound = false;
        List<User> users = databaseService.getUserTableInfo();
        for (User user : users) {
            matchFound = match(user, email, password);
            if(matchFound) break;
        }
        return matchFound;
    }

    /**
     * Registers a new user to the database, intended to be used on the register page
     * @param email String, email that will be associated with this new account
     * @param password String, password that will be associated with this new account
     * @return boolean, if user has been added and found in the database successfully, return true, otherwise, false
     */
    public boolean registerUser(String email, String password) {
        boolean isRegister = false;
        databaseService.addUserRecord(email, password);
        if(databaseService.getUserRecord(new User(email, password)) != null){
            isRegister = true;
        }
        return isRegister;
    }

    /**
     * Retrieves the user that is currently logged in, returns null if no user is logged in.
     * @return
     */
    public User getLogged() {
        return loggedAs;
    }

    /**
     * Sets the user that is currently logged in
     * @param loggedAs User, returns the user object that is currently logged in
     */
    public void setLogged(User loggedAs) {
        this.loggedAs = databaseService.getUserRecord(loggedAs);
    }

    /**
     * Private method to check if the email and password matches the user email and password
     * @param user User, the one
     * @param email
     * @param password
     * @return
     */
    private boolean match(User user, String email, String password){
        return user.getEmail().equals(email) && user.getPassword().equals(password);
    }
}
