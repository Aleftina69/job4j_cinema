package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.map.HallMapper;
import ru.job4j.cinema.model.Hall;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class Sql2oHallRepository implements HallRepository {

    private final Sql2o sql2o;

    public Sql2oHallRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Hall save(Hall hall) {
        String sql = "INSERT INTO halls (name, row_count, place_count, description) "
                + "VALUES (:name, :rowCount, :placeCount, :description)";
        try (Connection connection = sql2o.open()) {
            int id = (int) connection.createQuery(sql, true)
                    .addParameter("name", hall.getName())
                    .addParameter("rowCount", hall.getRowCount())
                    .addParameter("placeCount", hall.getPlaceCount())
                    .addParameter("description", hall.getDescription())
                    .executeUpdate()
                    .getKey();
            hall.setId(id);
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
        return hall;
    }

    @Override
    public Optional<Hall> findById(int id) {
        String sql = "SELECT * FROM halls WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            Hall hall = connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(new HallMapper());
            return Optional.ofNullable(hall);
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Hall> findAll() {
        String sql = "SELECT * FROM halls";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .executeAndFetch(new HallMapper())
                    .stream()
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public void deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM halls WHERE id = :id");
            query.addParameter("id", id).executeUpdate();
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
    }
}
