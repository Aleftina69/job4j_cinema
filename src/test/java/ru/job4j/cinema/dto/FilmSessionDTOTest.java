package ru.job4j.cinema.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FilmSessionDTOTest {

    @Test
    void testFilmSessionDTOBuilder() {
        LocalDateTime start = LocalDateTime.of(2025, 11, 11, 18, 0);
        LocalDateTime end = LocalDateTime.of(2025, 11, 11, 20, 30);

        FilmSessionDTO session = new FilmSessionDTO.Builder()
                .id(1)
                .filmName("Интерстеллар")
                .hallName("Зал 1")
                .startTime(start)
                .endTime(end)
                .price(500)
                .hallRowCount(10)
                .hallPlaceCount(20)
                .build();

        assertEquals(1, session.getId());
        assertEquals("Интерстеллар", session.getFilmName());
        assertEquals("Зал 1", session.getHallName());
        assertEquals(start, session.getStartTime());
        assertEquals(end, session.getEndTime());
        assertEquals(500, session.getPrice());
        assertEquals(10, session.getHallRowCount());
        assertEquals(20, session.getHallPlaceCount());
    }

    @Test
    void testFilmSessionDTOBuilderWithPartialFields() {
        FilmSessionDTO session = new FilmSessionDTO.Builder()
                .filmName("Тестовый фильм")
                .hallName("Зал 2")
                .price(300)
                .build();

        assertEquals(0, session.getId());
        assertEquals("Тестовый фильм", session.getFilmName());
        assertEquals("Зал 2", session.getHallName());
        assertNull(session.getStartTime());
        assertNull(session.getEndTime());
        assertEquals(300, session.getPrice());
        assertEquals(0, session.getHallRowCount());
        assertEquals(0, session.getHallPlaceCount());
    }

    @Test
    void testToString() {
        LocalDateTime start = LocalDateTime.of(2025, 11, 11, 18, 0);
        FilmSessionDTO session = new FilmSessionDTO.Builder()
                .id(1)
                .filmName("Интерстеллар")
                .hallName("Зал 1")
                .startTime(start)
                .price(500)
                .build();

        String toString = session.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("filmName='Интерстеллар'"));
        assertTrue(toString.contains("hallName='Зал 1'"));
        assertTrue(toString.contains("price=500"));
    }
}
