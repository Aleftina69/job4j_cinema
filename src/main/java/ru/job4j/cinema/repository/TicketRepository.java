package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    Optional<Ticket> save(Ticket ticket);

    Optional<Ticket> findById(int id);

    List<Ticket> findAll();

    boolean deleteById(int id);

    List<Ticket> findTicketsBySessionId(int sessionId);

    Optional<Ticket> findBySessionIdAndRowAndPlace(int sessionId, int rowNumber, int placeNumber);

    void deleteAll();
}

