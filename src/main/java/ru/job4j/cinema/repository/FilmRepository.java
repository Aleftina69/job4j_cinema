package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Film save(Film film);

    Optional<Film> findById(int id);

    List<Film> findAll();

    Film update(Film film);

    void delete(Film film);

    List<Film> findByGenreId(int genreId);
}
