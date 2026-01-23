package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.File;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class Sql2oFileRepository implements FileRepository {
    private Sql2o sql2o;

    public Sql2oFileRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public File save(File file) {
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO files(name, path)
                    VALUES (:name, :path)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            file.setId(generatedId);
            return file;
        }
    }

    @Override
    public Optional<File> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM files WHERE id = :id");
            var file = query.addParameter("id", id).executeAndFetchFirst(File.class);
            return Optional.ofNullable(file);
        }
    }

    @Override
    public void deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM files WHERE id = :id");
            query.addParameter("id", id).executeUpdate();
        }
    }

    @Override
    public Collection<File> findAll() {
        try (var connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM files").executeAndFetch(File.class);
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
        return List.of();
    }
}
