package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDTO;
import ru.job4j.cinema.service.FilmService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

class FilmControllerTest {
    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    @BeforeEach
    public void initServices() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetFilmsThenReturnFilmsListWithData() {
        var film1 = new FilmDTO.Builder()
                .id(1)
                .name("Фильм 1")
                .description("Описание 1")
                .year(2023)
                .genre("Жанр 1")
                .minAge(18)
                .duration(120)
                .fileId(1)
                .build();
        var film2 = new FilmDTO.Builder()
                .id(2)
                .name("Фильм 2")
                .description("Описание 2")
                .year(2024)
                .genre("Жанр 2")
                .minAge(16)
                .duration(90)
                .fileId(2)
                .build();
        var expectedFilms = List.of(film1, film2);

        when(filmService.findAll()).thenReturn(expectedFilms);

        var model = new ConcurrentModel();
        var viewName = filmController.getFilms(model);

        assertThat(viewName).isEqualTo("films/list");
        assertThat(model.getAttribute("films")).isEqualTo(expectedFilms);
    }
}
