package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.TicketPurchaseForm;
import ru.job4j.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketService {

    Optional<Ticket> buyTicket(TicketPurchaseForm form);

    Optional<Ticket> findById(int id);

    List<Ticket> findAll();

    List<Ticket> findTicketsBySessionId(int sessionId);

    void deleteById(int id);
}
