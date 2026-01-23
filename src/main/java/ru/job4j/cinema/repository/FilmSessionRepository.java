package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.FilmSession;

import java.util.List;
import java.util.Optional;

public interface FilmSessionRepository {

    FilmSession save(FilmSession filmSession);

    Optional<FilmSession> findById(int id);

    List<FilmSession> findAll();

    void deleteById(int id);

    List<FilmSession> findByFilmId(int filmId);
}