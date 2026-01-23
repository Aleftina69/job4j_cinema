package ru.job4j.cinema.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDTO;
import ru.job4j.cinema.repository.FilmRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimpleFilmService implements FilmService {

    private final FilmRepository filmRepository;
    private final GenreService genreService;

    public SimpleFilmService(
            @Qualifier("sql2oFilmRepository") FilmRepository filmRepository,
            GenreService genreService) {
        this.filmRepository = filmRepository;
        this.genreService = genreService;
    }

    @Override
    public Optional<FilmDTO> findById(int id) {
        return filmRepository.findById(id)
                .flatMap(film ->
                        genreService.findById(film.getGenreId())
                                .map(genre -> new FilmDTO.Builder()
                                        .id(film.getId())
                                        .name(film.getName())
                                        .description(film.getDescription())
                                        .year(film.getYear())
                                        .genre(genre.getName())
                                        .minAge(film.getMinimalAge())
                                        .duration(film.getDurationInMinutes())
                                        .fileId(film.getFileId())
                                        .build()
                                )
                );
    }

    @Override
    public Collection<FilmDTO> findAll() {
        return filmRepository.findAll().stream()
                .map(film -> {
                    // Пытаемся найти жанр, если не нашли — используем заглушку "Неизвестно"
                    String genreName = genreService.findById(film.getGenreId())
                            .map(genre -> genre.getName())
                            .orElse("Неизвестно");

                    return new FilmDTO.Builder()
                            .id(film.getId())
                            .name(film.getName())
                            .description(film.getDescription())
                            .year(film.getYear())
                            .genre(genreName) // Передаем имя жанра или "Неизвестно"
                            .minAge(film.getMinimalAge())
                            .duration(film.getDurationInMinutes())
                            .fileId(film.getFileId())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
