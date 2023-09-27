package com.groupone.service;

import com.groupone.api.DatabaseAPI;
import com.groupone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class UserService {

    @Autowired
    DatabaseService databaseService;

    User loggedAs = null;

    public boolean authenticate(String email, String password) { // TODO, check if the account is locked
        boolean matchFound = false;
        List<User> users = databaseService.getUserTableInfo();
        for (User user : users) {
            matchFound = match(user, email, password);
            if(matchFound){
                break;
            }
        }
        return matchFound;
    }

    public User getLogged() {
        return loggedAs;
    }

    public void setLogged(User loggedAs) {
        this.loggedAs = databaseService.getUserRecord(loggedAs);
    }

    private boolean match(User user, String email, String password){
        return user.getEmail().equals(email) && user.getPassword().equals(password);
    }
}
