package com.groupone.service;

import com.groupone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User> getUserTableInfo(){
        String selectSql = "SELECT * FROM user";
        return queryForUsers(selectSql);
    }

    public User getUser(int userid) throws Exception{
        String selectSql = "SELECT * FROM user WHERE ROWID=" + userid;
        List<User> result = queryForUsers(selectSql);
        if(result.size() == 0) throw new Exception("User Not Found!");
        return result.get(0);
    }

    private List<User> queryForUsers(String query){
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new User(
                    rowNum,
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getBoolean("isLocked")
        ));
    }
}
