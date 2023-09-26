package com.groupone.service;

import com.groupone.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class UserService {

    public List<User> getUsers() {
        //TODO, interact with db to get users in database
        return null;
    }

    public boolean authenticate(String email, String password) {
        //TODO, write method body
        return false;
    }
}
