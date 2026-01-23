package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.dto.TicketPurchaseForm;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final FilmSessionService filmSessionService;

    public TicketController(TicketService ticketService, FilmSessionService filmSessionService) {
        this.ticketService = ticketService;
        this.filmSessionService = filmSessionService;
    }

    @GetMapping("/purchase")
    public String showPurchaseForm(@RequestParam int sessionId,
                                   @RequestParam(required = false) Integer rowNumber,
                                   @RequestParam(required = false) Integer placeNumber,
                                   Model model) {
        Optional<FilmSessionDTO> sessionOptional = filmSessionService.findById(sessionId);
        if (sessionOptional.isEmpty()) {
            model.addAttribute("message", "Сеанс не найден.");
            return "redirect:/sessions";
        }

        FilmSessionDTO filmSession = sessionOptional.get();

        Map<Integer, Set<String>> occupiedPlacesBySession = filmSessionService.findAll().stream()
                .collect(Collectors.toMap(
                        FilmSessionDTO::getId,
                        filmSessionDTO -> ticketService.findTicketsBySessionId(filmSessionDTO.getId()).stream()
                                .map(ticket -> ticket.getRowNumber() + "," + ticket.getPlaceNumber())
                                .collect(Collectors.toSet())
                ));
        if (rowNumber != null && placeNumber != null) {
            TicketPurchaseForm form = new TicketPurchaseForm();
            form.setSessionId(sessionId);
            form.setRowNumber(rowNumber);
            form.setPlaceNumber(placeNumber);
            model.addAttribute("ticketForm", form);
            model.addAttribute("selectedRow", rowNumber);
            model.addAttribute("selectedPlace", placeNumber);
        } else {
            TicketPurchaseForm form = new TicketPurchaseForm();
            form.setSessionId(sessionId);
            model.addAttribute("ticketForm", form);
            model.addAttribute("selectedRow", null);
            model.addAttribute("selectedPlace", null);
        }

        model.addAttribute("sessions", filmSessionService.findAll());
        model.addAttribute("occupiedPlacesBySession", occupiedPlacesBySession);
        model.addAttribute("hallRowCount", filmSession.getHallRowCount());
        model.addAttribute("hallPlaceCount", filmSession.getHallPlaceCount());
        model.addAttribute("filmSession", filmSession);
        return "tickets/purchase";
    }

    @PostMapping("/purchase")
    public String processPurchase(@ModelAttribute("ticketForm") TicketPurchaseForm form,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        Optional<Ticket> purchasedTicket = ticketService.buyTicket(form);
        if (purchasedTicket.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "Билет успешно куплен! Номер билета: " + purchasedTicket.get().getId());
            return "redirect:/sessions";
        } else {
            model.addAttribute("errorMessage", "Ошибка при покупке билета. Возможно, место уже занято или произошла другая ошибка.");
            Optional<FilmSessionDTO> sessionOptional = filmSessionService.findById(form.getSessionId());
            if (sessionOptional.isPresent()) {
                model.addAttribute("filmSession", sessionOptional.get());
            }
            return "tickets/purchase";
        }
    }
}
