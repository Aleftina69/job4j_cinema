package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.service.FilmSessionService;

import java.util.Collection;

@Controller
@RequestMapping("/sessions")
public class FilmSessionController {

    private final FilmSessionService filmSessionService;

    public FilmSessionController(FilmSessionService filmSessionService) {
        this.filmSessionService = filmSessionService;
    }

    @GetMapping
    public String getAllSessions(Model model, @ModelAttribute("message") String message) {
        Collection<FilmSessionDTO> sessions = filmSessionService.findAll();
        model.addAttribute("sessions", sessions);
        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
        }
        return "sessions/list";
    }
}
