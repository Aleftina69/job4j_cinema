package ru.job4j.cinema.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.HallRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SimpleFilmSessionService implements FilmSessionService {
    private final FilmSessionRepository filmSessionRepository;
    private final FilmRepository filmRepository;
    private final HallRepository hallRepository;

    public SimpleFilmSessionService(
            @Qualifier("sql2oFilmSessionRepository") FilmSessionRepository filmSessionRepository,
            @Qualifier("sql2oFilmRepository") FilmRepository filmRepository,
            @Qualifier("sql2oHallRepository") HallRepository hallRepository
    ) {
        this.filmSessionRepository = filmSessionRepository;
        this.filmRepository = filmRepository;
        this.hallRepository = hallRepository;
    }

    private Optional<FilmSessionDTO> convertToDTO(FilmSession session) {
        Optional<Film> filmOptional = filmRepository.findById(session.getFilmId());
        Optional<Hall> hallOptional = hallRepository.findById(session.getHallId());

        if (filmOptional.isEmpty() || hallOptional.isEmpty()) {
            return Optional.empty();
        }

        Film film = filmOptional.get();
        Hall hall = hallOptional.get();

        return Optional.of(new FilmSessionDTO.Builder()
                .id(session.getId())
                .filmName(film.getName())
                .hallName(hall.getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .price(session.getPrice())
                .hallRowCount(hall.getRowCount())
                .hallPlaceCount(hall.getPlaceCount())
                .build());
    }

    @Override
    public Optional<FilmSessionDTO> findById(int id) {
        return filmSessionRepository.findById(id)
                .flatMap(this::convertToDTO);
    }

    @Override
    public Collection<FilmSessionDTO> findAll() {
        return filmSessionRepository.findAll().stream()
                .flatMap(session -> convertToDTO(session).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<FilmSessionDTO> findDTOsByFilmId(int filmId) {
        return filmSessionRepository.findByFilmId(filmId).stream()
                .flatMap(session -> convertToDTO(session).stream())
                .collect(Collectors.toList());
    }
}
