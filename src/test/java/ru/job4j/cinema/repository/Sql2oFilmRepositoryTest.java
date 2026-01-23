package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.job4j.cinema.map.FilmMapper;
import ru.job4j.cinema.model.Film;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Sql2oFilmRepositoryTest {

    private Sql2oFilmRepository sql2oFilmRepository;
    private Sql2o sql2o;
    private FilmMapper filmMapper;

    @BeforeEach
    void setUp() {
        String dbName = "testdb_" + this.getClass().getSimpleName();
        String jdbcUrl = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        sql2o = new Sql2o("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        filmMapper = new FilmMapper();
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o, filmMapper);
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS films").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS genres").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS files").executeUpdate();
            connection.createQuery("""
                    CREATE TABLE genres ( id SERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL)""").executeUpdate();
            connection.createQuery("""
                    CREATE TABLE files (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        path VARCHAR(255) NOT NULL
                    )
                    """).executeUpdate();
            connection.createQuery("""
                    CREATE TABLE films (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        "year" INTEGER NOT NULL,
                        genre_id INTEGER REFERENCES genres(id),
                        minimal_age INTEGER,
                        duration_in_minutes INTEGER,
                        file_id INTEGER REFERENCES files(id)
                    )
                    """).executeUpdate();
            connection.createQuery("INSERT INTO genres (name) VALUES (:name)")
                    .addParameter("name", "Action")
                    .executeUpdate();
            connection.createQuery("INSERT INTO files (name, path) VALUES (:name, :path)")
                    .addParameter("name", "poster.jpg")
                    .addParameter("path", "/path/to/poster.jpg")
                    .executeUpdate();
        }
    }

    @AfterEach
    void tearDown() {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM films").executeUpdate();
            connection.createQuery("DELETE FROM genres").executeUpdate();
            connection.createQuery("DELETE FROM files").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS films").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS genres").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS files").executeUpdate();
        }
    }

    @Test
    void whenSaveThenGetSame() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setYear(2023);
        film.setGenreId(1);
        film.setMinimalAge(18);
        film.setDurationInMinutes(120);
        film.setFileId(1);

        sql2oFilmRepository.save(film);
        Film savedFilm = sql2oFilmRepository.findById(film.getId()).get();

        assertThat(savedFilm).isEqualTo(film);
    }

    @Test
    void whenFindByIdThenGetFilm() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setYear(2023);
        film.setGenreId(1);
        film.setMinimalAge(18);
        film.setDurationInMinutes(120);
        film.setFileId(1);
        sql2oFilmRepository.save(film);
        Optional<Film> foundFilm = sql2oFilmRepository.findById(film.getId());
        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get()).isEqualTo(film);
    }

    @Test
    void whenFindAllThenGetAllFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setYear(2023);
        film1.setGenreId(1);
        film1.setMinimalAge(18);
        film1.setDurationInMinutes(120);
        film1.setFileId(1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setYear(2024);
        film2.setGenreId(1);
        film2.setMinimalAge(16);
        film2.setDurationInMinutes(90);
        film2.setFileId(1);

        sql2oFilmRepository.save(film1);
        sql2oFilmRepository.save(film2);
        List<Film> films = sql2oFilmRepository.findAll();

        assertThat(films).hasSize(2);
        assertThat(films).contains(film1, film2);
    }

    @Test
    void whenUpdateThenGetUpdated() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setYear(2023);
        film.setGenreId(1);
        film.setMinimalAge(18);
        film.setDurationInMinutes(120);
        film.setFileId(1);

        sql2oFilmRepository.save(film);
        film.setName("Updated Name");
        film.setDescription("Updated Description");
        sql2oFilmRepository.update(film);
        Film updatedFilm = sql2oFilmRepository.findById(film.getId()).get();

        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setYear(2023);
        film.setGenreId(1);
        film.setMinimalAge(18);
        film.setDurationInMinutes(120);
        film.setFileId(1);

        sql2oFilmRepository.save(film);
        sql2oFilmRepository.delete(film);
        Optional<Film> deletedFilm = sql2oFilmRepository.findById(film.getId());

        assertThat(deletedFilm).isEmpty();
    }

    @Test
    void whenFindByGenreIdThenGetFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setYear(2023);
        film1.setGenreId(1);
        film1.setMinimalAge(18);
        film1.setDurationInMinutes(120);
        film1.setFileId(1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setYear(2024);
        film2.setGenreId(1);
        film2.setMinimalAge(16);
        film2.setDurationInMinutes(90);
        film2.setFileId(1);

        sql2oFilmRepository.save(film1);
        sql2oFilmRepository.save(film2);
        List<Film> filmsByGenre = sql2oFilmRepository.findByGenreId(1);

        assertThat(filmsByGenre).hasSize(2);
        assertThat(filmsByGenre).contains(film1, film2);
    }
}
