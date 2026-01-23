package ru.job4j.cinema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.dto.FilmDTO;
import ru.job4j.cinema.service.FilmService;

import java.util.Collection;

@Controller
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @GetMapping("/list")
    public String getFilms(Model model) {
        Collection<FilmDTO> films = filmService.findAll();
        model.addAttribute("films", films);
        return "films/list";
    }
}