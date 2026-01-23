package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;

@Repository
public class Sql2oTicketRepository implements TicketRepository {

    private final Sql2o sql2o;

    public Sql2oTicketRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO tickets(session_id, row_number, place_number, user_id)
                    VALUES (:sessionId, :rowNumber, :placeNumber, :userId)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("sessionId", ticket.getSessionId())
                    .addParameter("rowNumber", ticket.getRowNumber())
                    .addParameter("placeNumber", ticket.getPlaceNumber())
                    .addParameter("userId", ticket.getUserId());

            try {
                int generatedId = query.executeUpdate().getKey(Integer.class);
                ticket.setId(generatedId);
                return Optional.of(ticket);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Ticket> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("""
                            SELECT t.id, t.session_id as sessionId, t.row_number as rowNumber, 
                                   t.place_number as placeNumber, t.user_id as userId, fs.price 
                            FROM tickets t 
                            JOIN film_sessions fs ON t.session_id = fs.id 
                            WHERE t.id = :id
                            """)
                    .addParameter("id", id);

            return Optional.ofNullable(query.executeAndFetchFirst(Ticket.class));
        }
    }

    @Override
    public List<Ticket> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("""
                    SELECT t.id, t.session_id as sessionId, t.row_number as rowNumber, 
                           t.place_number as placeNumber, t.user_id as userId, fs.price 
                    FROM tickets t 
                    JOIN film_sessions fs ON t.session_id = fs.id
                    """);

            return query.executeAndFetch(Ticket.class);
        }
    }

    @Override
    public List<Ticket> findTicketsBySessionId(int sessionId) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("""
                            SELECT t.id, t.session_id as sessionId, t.row_number as rowNumber, 
                                   t.place_number as placeNumber, t.user_id as userId, fs.price 
                            FROM tickets t 
                            JOIN film_sessions fs ON t.session_id = fs.id 
                            WHERE t.session_id = :sessionId
                            """)
                    .addParameter("sessionId", sessionId);

            return query.executeAndFetch(Ticket.class);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM tickets WHERE id = :id");
            int affectedRows = query.addParameter("id", id).executeUpdate().getResult();
            return affectedRows > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<Ticket> findBySessionIdAndRowAndPlace(int sessionId, int rowNumber, int placeNumber) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                            "SELECT t.id, t.session_id as sessionId, t.row_number as rowNumber, "
                                    + "t.place_number as placeNumber, t.user_id as userId "
                                    + "FROM tickets t "
                                    + "WHERE t.session_id = :sessionId AND t.row_number = :rowNumber AND t.place_number = :placeNumber")
                    .addParameter("sessionId", sessionId)
                    .addParameter("rowNumber", rowNumber)
                    .addParameter("placeNumber", placeNumber);

            return Optional.ofNullable(query.executeAndFetchFirst(Ticket.class));
        }
    }

    @Override
    public void deleteAll() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM tickets").executeUpdate();
        }
    }
}
