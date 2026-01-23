package ru.job4j.cinema.map;

import org.sql2o.ResultSetHandler;
import ru.job4j.cinema.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMapper implements ResultSetHandler<Film> {
    @Override
    public Film handle(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setYear(rs.getInt("year"));
        film.setGenreId(rs.getInt("genre_id"));
        film.setMinimalAge(rs.getInt("minimal_age"));
        film.setDurationInMinutes(rs.getInt("duration_in_minutes"));
        film.setFileId(rs.getInt("file_id"));
        return film;
    }
}
