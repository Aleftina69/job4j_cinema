package ru.job4j.cinema.map;

import org.sql2o.ResultSetHandler;
import ru.job4j.cinema.model.Hall;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallMapper implements ResultSetHandler<Hall> {
    @Override
    public Hall handle(ResultSet rs) throws SQLException {
        Hall hall = new Hall();
        hall.setId(rs.getInt("id"));
        hall.setName(rs.getString("name"));
        hall.setRowCount(rs.getInt("row_count"));
        hall.setPlaceCount(rs.getInt("place_count"));
        hall.setDescription(rs.getString("description"));
        return hall;
    }
}