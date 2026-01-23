package ru.job4j.cinema.map;

import org.sql2o.ResultSetHandler;
import ru.job4j.cinema.model.FilmSession;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmSessionMapper implements ResultSetHandler<FilmSession> {
    @Override
    public FilmSession handle(ResultSet rs) throws SQLException {
        FilmSession session = new FilmSession();
        session.setId(rs.getInt("id"));
        session.setFilmId(rs.getInt("film_id"));
        session.setHallId(rs.getInt("hall_id"));
        session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        session.setPrice(rs.getInt("price"));
        return session;
    }
}
