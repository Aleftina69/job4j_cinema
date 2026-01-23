package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    Genre save(Genre genre);

    Optional<Genre> findById(int id);

    List<Genre> findAll();

    void deleteById(int id);
}

