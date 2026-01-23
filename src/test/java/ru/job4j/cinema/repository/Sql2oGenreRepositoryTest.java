package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Sql2oGenreRepositoryTest {

    private Sql2oGenreRepository sql2oGenreRepository;
    private Sql2o sql2o;

    @BeforeEach
    void setUp() {
        String dbName = "testdb_" + this.getClass().getSimpleName();
        String jdbcUrl = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        sql2o = new Sql2o("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);

        try (Connection connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS genres").executeUpdate();

            connection.createQuery("""
                    CREATE TABLE genres (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
                    )
                    """).executeUpdate();

            connection.createQuery("INSERT INTO genres (name) VALUES (:name)")
                    .addParameter("name", "Action")
                    .executeUpdate();
            connection.createQuery("INSERT INTO genres (name) VALUES (:name)")
                    .addParameter("name", "Comedy")
                    .executeUpdate();
        }
    }

    @AfterEach
    void tearDown() {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM genres").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS genres").executeUpdate();
        }
    }

    @Test
    void whenSaveThenGetSame() {
        Genre genre = new Genre();
        genre.setName("Drama");

        Genre savedGenre = sql2oGenreRepository.save(genre);
        Genre foundGenre = sql2oGenreRepository.findById(savedGenre.getId()).get();

        assertThat(foundGenre.getName()).isEqualTo("Drama");
    }

    @Test
    void whenFindByIdThenGetGenre() {
        Genre genre = new Genre();
        genre.setName("Horror");

        Genre savedGenre = sql2oGenreRepository.save(genre);
        Optional<Genre> foundGenre = sql2oGenreRepository.findById(savedGenre.getId());

        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getName()).isEqualTo("Horror");
    }

    @Test
    void whenFindAllThenGetAllGenres() {
        List<Genre> genres = sql2oGenreRepository.findAll();

        assertThat(genres).hasSizeGreaterThanOrEqualTo(2);
        assertThat(genres.stream().anyMatch(g -> "Action".equals(g.getName()))).isTrue();
        assertThat(genres.stream().anyMatch(g -> "Comedy".equals(g.getName()))).isTrue();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        Genre genre = new Genre();
        genre.setName("Thriller");

        Genre savedGenre = sql2oGenreRepository.save(genre);
        sql2oGenreRepository.deleteById(savedGenre.getId());
        Optional<Genre> deletedGenre = sql2oGenreRepository.findById(savedGenre.getId());

        assertThat(deletedGenre).isEmpty();
    }
}
