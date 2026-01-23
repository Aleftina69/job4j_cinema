package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class Sql2oGenreRepository implements GenreRepository {

    private Sql2o sql2o;

    public Sql2oGenreRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Genre save(Genre genre) {
        String sql = "INSERT INTO genres (name) VALUES (:name)";
        try (Connection connection = sql2o.open()) {
            int id = (int) connection.createQuery(sql, true)
                    .addParameter("name", genre.getName())
                    .executeUpdate()
                    .getKey();
            genre.setId(id);
        }
        return genre;
    }

    @Override
    public Optional<Genre> findById(int id) {
        String sql = "SELECT * FROM genres WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            Genre genre = connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Genre.class);
            return Optional.ofNullable(genre);
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql).executeAndFetch(Genre.class);
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public void deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM genres WHERE id = :id");
            query.addParameter("id", id).executeUpdate();
        }
    }
}
