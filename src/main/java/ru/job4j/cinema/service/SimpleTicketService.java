package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.cinema.dto.RegistrationForm;
import ru.job4j.cinema.dto.TicketPurchaseForm;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SimpleTicketService implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final FilmSessionService filmSessionService;

    public SimpleTicketService(TicketRepository ticketRepository, UserService userService, FilmSessionService filmSessionService) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.filmSessionService = filmSessionService;
    }

    @Override
    @Transactional
    public Optional<Ticket> buyTicket(TicketPurchaseForm form) {
        if (ticketRepository.findBySessionIdAndRowAndPlace(form.getSessionId(), form.getRowNumber(), form.getPlaceNumber()).isPresent()) {
            return Optional.empty();
        }
        Optional<User> userOpt = userService.findByEmail(form.getEmail());
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            RegistrationForm registrationForm = new RegistrationForm();
            registrationForm.setFullName(form.getFullName());
            registrationForm.setEmail(form.getEmail());
            registrationForm.setPassword("");

            Optional<User> registeredUser = userService.register(registrationForm);
            if (registeredUser.isEmpty()) {
                return Optional.empty();
            }
            user = registeredUser.get();
        }

        int ticketPrice = filmSessionService.findById(form.getSessionId())
                .map(session -> session.getPrice())
                .orElse(0);

        Ticket newTicket = new Ticket();
        newTicket.setSessionId(form.getSessionId());
        newTicket.setRowNumber(form.getRowNumber());
        newTicket.setPlaceNumber(form.getPlaceNumber());
        newTicket.setUserId(user.getId());
        newTicket.setPrice(ticketPrice);
        return ticketRepository.save(newTicket);
    }

    @Override
    public Optional<Ticket> findById(int id) {
        return ticketRepository.findById(id);
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> findTicketsBySessionId(int sessionId) {
        return ticketRepository.findTicketsBySessionId(sessionId);
    }

    @Override
    public void deleteById(int id) {
        ticketRepository.deleteById(id);
    }
}
