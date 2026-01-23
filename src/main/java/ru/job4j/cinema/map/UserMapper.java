package ru.job4j.cinema.map;

import org.sql2o.ResultSetHandler;
import ru.job4j.cinema.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetHandler<User> {
    @Override
    public User handle(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        return user;
    }
}
