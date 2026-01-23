package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmDTO;

import java.util.Collection;
import java.util.Optional;

public interface FilmService {
    Optional<FilmDTO> findById(int id);

    Collection<FilmDTO> findAll();
}

