package ru.job4j.cinema.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilmDTOTest {

    @Test
    void testFilmDTOBuilder() {
        FilmDTO film = new FilmDTO.Builder()
                .id(1)
                .name("Интерстеллар")
                .description("Фантастический фильм о космосе")
                .year(2014)
                .genre("Sci-Fi")
                .minAge(12)
                .duration(169)
                .fileId(123)
                .build();

        assertEquals(1, film.getId());
        assertEquals("Интерстеллар", film.getName());
        assertEquals("Фантастический фильм о космосе", film.getDescription());
        assertEquals(2014, film.getYear());
        assertEquals("Sci-Fi", film.getGenre());
        assertEquals(12, film.getMinAge());
        assertEquals(169, film.getDuration());
        assertEquals(123, film.getFileId());
    }

    @Test
    void testFilmDTOBuilderWithPartialFields() {
        FilmDTO film = new FilmDTO.Builder()
                .name("Тестовый фильм")
                .genre("Драма")
                .build();

        assertEquals(0, film.getId());
        assertEquals("Тестовый фильм", film.getName());
        assertNull(film.getDescription());
        assertEquals(0, film.getYear());
        assertEquals("Драма", film.getGenre());
        assertEquals(0, film.getMinAge());
        assertEquals(0, film.getDuration());
        assertEquals(0, film.getFileId());
    }

    @Test
    void testEqualsAndHashCode() {
        FilmDTO film1 = new FilmDTO.Builder().id(1).name("Фильм").build();
        FilmDTO film2 = new FilmDTO.Builder().id(1).name("Другой").build();
        FilmDTO film3 = new FilmDTO.Builder().id(2).name("Фильм").build();

        assertEquals(film1, film2);
        assertEquals(film1.hashCode(), film2.hashCode());
        assertNotEquals(film1, film3);
    }
}
