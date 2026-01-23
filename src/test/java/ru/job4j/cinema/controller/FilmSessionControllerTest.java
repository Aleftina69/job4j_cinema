package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.service.FilmSessionService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

class FilmSessionControllerTest {
    @Mock
    private FilmSessionService filmSessionService;

    @InjectMocks
    private FilmSessionController filmSessionController;

    @BeforeEach
    public void initServices() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetAllSessionsWithoutMessageThenReturnSessionsListWithData() {
        var session1 = new FilmSessionDTO.Builder()
                .id(1)
                .filmName("Фильм 1")
                .hallName("Зал 1")
                .startTime(LocalDateTime.parse("2023-10-01T10:00"))
                .endTime(LocalDateTime.parse("2023-10-01T12:00"))
                .price(500)
                .build();
        var session2 = new FilmSessionDTO.Builder()
                .id(2)
                .filmName("Фильм 2")
                .hallName("Зал 2")
                .startTime(LocalDateTime.parse("2023-10-01T14:00"))
                .endTime(LocalDateTime.parse("2023-10-01T16:00"))
                .price(600)
                .build();
        var expectedSessions = List.of(session1, session2);

        when(filmSessionService.findAll()).thenReturn(expectedSessions);

        var model = new ConcurrentModel();
        var viewName = filmSessionController.getAllSessions(model, null);

        assertThat(viewName).isEqualTo("sessions/list");
        assertThat(model.getAttribute("sessions")).isEqualTo(expectedSessions);
        assertThat(model.getAttribute("message")).isNull();
    }

    @Test
    void whenGetAllSessionsWithMessageThenReturnSessionsListWithDataAndMessage() {
        var session1 = new FilmSessionDTO.Builder()
                .id(1)
                .filmName("Фильм 1")
                .hallName("Зал 1")
                .startTime(LocalDateTime.parse("2023-10-01T10:00"))
                .endTime(LocalDateTime.parse("2023-10-01T12:00"))
                .price(500)
                .build();
        var session2 = new FilmSessionDTO.Builder()
                .id(2)
                .filmName("Фильм 2")
                .hallName("Зал 2")
                .startTime(LocalDateTime.parse("2023-10-01T14:00"))
                .endTime(LocalDateTime.parse("2023-10-01T16:00"))
                .price(600)
                .build();
        var expectedSessions = List.of(session1, session2);
        var message = "Тестовое сообщение";

        when(filmSessionService.findAll()).thenReturn(expectedSessions);

        var model = new ConcurrentModel();
        var viewName = filmSessionController.getAllSessions(model, message);

        assertThat(viewName).isEqualTo("sessions/list");
        assertThat(model.getAttribute("sessions")).isEqualTo(expectedSessions);
        assertThat(model.getAttribute("message")).isEqualTo(message);
    }
}
