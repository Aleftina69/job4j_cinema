package ru.job4j.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.map.FilmMapper;
import ru.job4j.cinema.model.Film;

import java.util.List;
import java.util.Optional;

@Repository
public class Sql2oFilmRepository implements FilmRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Sql2oFilmRepository.class);

    private final Sql2o sql2o;
    private final FilmMapper filmMapper;

    public Sql2oFilmRepository(Sql2o sql2o, FilmMapper filmMapper) {
        this.sql2o = sql2o;
        this.filmMapper = filmMapper;
    }

    @Override
    public Film save(Film film) {
        String sql = "INSERT INTO films (name, description, \"year\", genre_id, minimal_age, duration_in_minutes, file_id)"
                + " VALUES (:name, :description, :year, :genreId, :minimalAge, :durationInMinutes, :fileId)";
        try (Connection connection = sql2o.open()) {
            int id = (int) connection.createQuery(sql, true)
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("year", film.getYear())
                    .addParameter("genreId", film.getGenreId())
                    .addParameter("minimalAge", film.getMinimalAge())
                    .addParameter("durationInMinutes", film.getDurationInMinutes())
                    .addParameter("fileId", film.getFileId())
                    .executeUpdate()
                    .getKey();
            film.setId(id);
        } catch (Sql2oException e) {
            LOG.error("Error saving film: {}", film, e);
            throw new RuntimeException("Could not save film: " + film.getName(), e);
        }
        return film;
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "SELECT * FROM films WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            Film film = connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(filmMapper);
            return Optional.ofNullable(film);
        } catch (Sql2oException e) {
            LOG.error("Error finding film by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .executeAndFetch(filmMapper);
        } catch (Sql2oException e) {
            LOG.error("Error finding all films", e);
            return List.of();
        }
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = :name, description = :description, \"year\" = :year, "
                + "genre_id = :genreId, minimal_age = :minimalAge, duration_in_minutes = :durationInMinutes, file_id = :fileId "
                + "WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("year", film.getYear())
                    .addParameter("genreId", film.getGenreId())
                    .addParameter("minimalAge", film.getMinimalAge())
                    .addParameter("durationInMinutes", film.getDurationInMinutes())
                    .addParameter("fileId", film.getFileId())
                    .addParameter("id", film.getId())
                    .executeUpdate();
        } catch (Sql2oException e) {
            LOG.error("Error updating film: {}", film, e);
            throw new RuntimeException("Could not update film: " + film.getId(), e);
        }
        return film;
    }

    @Override
    public void delete(Film film) {
        String sql = "DELETE FROM films WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", film.getId())
                    .executeUpdate();
        } catch (Sql2oException e) {
            LOG.error("Error deleting film: {}", film, e);
            throw new RuntimeException("Could not delete film: " + film.getId(), e);
        }
    }

    @Override
    public List<Film> findByGenreId(int genreId) {
        String sql = "SELECT * FROM films WHERE genre_id = :genreId";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("genreId", genreId)
                    .executeAndFetch(filmMapper);
        } catch (Sql2oException e) {
            LOG.error("Error finding films by genreId: {}", genreId, e);
            return List.of();
        }
    }
}