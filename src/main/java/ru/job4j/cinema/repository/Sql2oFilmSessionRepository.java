package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.map.FilmSessionMapper;
import ru.job4j.cinema.model.FilmSession;

import java.util.List;
import java.util.Optional;

@Repository
public class Sql2oFilmSessionRepository implements FilmSessionRepository {

    private final Sql2o sql2o;

    public Sql2oFilmSessionRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public FilmSession save(FilmSession filmSession) {
        String sql = "INSERT INTO film_sessions (film_id, hall_id, start_time, end_time, price) "
                + "VALUES (:filmId, :hallId, :startTime, :endTime, :price)";
        try (Connection connection = sql2o.open()) {
            int id = (int) connection.createQuery(sql, true)
                    .addParameter("filmId", filmSession.getFilmId())
                    .addParameter("hallId", filmSession.getHallId())
                    .addParameter("startTime", filmSession.getStartTime())
                    .addParameter("endTime", filmSession.getEndTime())
                    .addParameter("price", filmSession.getPrice())
                    .executeUpdate()
                    .getKey();
            filmSession.setId(id);
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
        return filmSession;
    }

    @Override
    public Optional<FilmSession> findById(int id) {
        String sql = "SELECT * FROM film_sessions WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            FilmSession filmSession = connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(new FilmSessionMapper());
            return Optional.ofNullable(filmSession);
        } catch (Sql2oException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<FilmSession> findAll() {
        String sql = "SELECT * FROM film_sessions";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql).executeAndFetch(new FilmSessionMapper());
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public void deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM film_sessions WHERE id = :id");
            query.addParameter("id", id).executeUpdate();
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<FilmSession> findByFilmId(int filmId) {
        String sql = "SELECT * FROM film_sessions WHERE film_id = :filmId";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("filmId", filmId)
                    .executeAndFetch(new FilmSessionMapper());
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
        return List.of();
    }
}