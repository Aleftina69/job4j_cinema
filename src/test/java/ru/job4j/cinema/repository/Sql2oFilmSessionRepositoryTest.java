package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.job4j.cinema.map.FilmSessionMapper;
import ru.job4j.cinema.model.FilmSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Sql2oFilmSessionRepositoryTest {

    private Sql2oFilmSessionRepository sql2oFilmSessionRepository;
    private Sql2o sql2o;
    private FilmSessionMapper filmSessionMapper;

    @BeforeEach
    void setUp() {
        String dbName = "testdb_" + this.getClass().getSimpleName();
        String jdbcUrl = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        sql2o = new Sql2o("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        filmSessionMapper = new FilmSessionMapper();
        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);

        try (Connection connection = sql2o.open()) {
            dropTables(connection);
            createTables(connection);
            insertTestData(connection);
        }
    }

    private void dropTables(Connection connection) {
        String[] tables = {"film_sessions", "films", "halls", "genres", "files"};
        for (String table : tables) {
            connection.createQuery("DROP TABLE IF EXISTS " + table).executeUpdate();
        }
    }

    private void createTables(Connection connection) {
        connection.createQuery("""
                CREATE TABLE genres (id SERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL) 
                """).executeUpdate();
        connection.createQuery("""
                CREATE TABLE files (id SERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL, path VARCHAR(255) NOT NULL) 
                """).executeUpdate();
        connection.createQuery("""
                CREATE TABLE films (id SERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL,
                    description TEXT, "year" INTEGER NOT NULL, genre_id INTEGER REFERENCES genres(id),
                    minimal_age INTEGER, duration_in_minutes INTEGER, file_id INTEGER REFERENCES files(id))
                """).executeUpdate();
        connection.createQuery("""
                CREATE TABLE halls (id SERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL,
                    row_count INTEGER NOT NULL, place_count INTEGER NOT NULL, description TEXT)
                """).executeUpdate();
        connection.createQuery("""
                CREATE TABLE film_sessions (id SERIAL PRIMARY KEY, film_id INTEGER REFERENCES films(id),
                    hall_id INTEGER REFERENCES halls(id), start_time TIMESTAMP NOT NULL, 
                    end_time TIMESTAMP NOT NULL, price INTEGER NOT NULL)
                """).executeUpdate();
    }

    private void insertTestData(Connection connection) {
        connection.createQuery("INSERT INTO genres (name) VALUES (:name)")
                .addParameter("name", "Action")
                .executeUpdate();
        connection.createQuery("INSERT INTO files (name, path) VALUES (:name, :path)")
                .addParameter("name", "poster.jpg")
                .addParameter("path", "/path/to/poster.jpg")
                .executeUpdate();
        connection.createQuery("INSERT INTO films (name, description, \"year\", genre_id, minimal_age, duration_in_minutes, file_id) VALUES (:name, :description, :year, :genreId, :minimalAge, :duration, :fileId)")
                .addParameter("name", "Test Film")
                .addParameter("description", "Test Description")
                .addParameter("year", 2023)
                .addParameter("genreId", 1)
                .addParameter("minimalAge", 18)
                .addParameter("duration", 120)
                .addParameter("fileId", 1)
                .executeUpdate();
        connection.createQuery("INSERT INTO halls (name, row_count, place_count, description) VALUES (:name, :rowCount, :placeCount, :description)")
                .addParameter("name", "Hall 1")
                .addParameter("rowCount", 10)
                .addParameter("placeCount", 20)
                .addParameter("description", "Test Hall")
                .executeUpdate();
    }

    @AfterEach
    void tearDown() {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM film_sessions").executeUpdate();
            connection.createQuery("DELETE FROM films").executeUpdate();
            connection.createQuery("DELETE FROM halls").executeUpdate();
            connection.createQuery("DELETE FROM genres").executeUpdate();
            connection.createQuery("DELETE FROM files").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS film_sessions").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS films").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS halls").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS genres").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS files").executeUpdate();
        }
    }

    @Test
    void whenSaveThenGetSame() {
        FilmSession session = new FilmSession();
        session.setFilmId(1);
        session.setHallId(1);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now().plusHours(2));
        session.setPrice(500);

        sql2oFilmSessionRepository.save(session);
        FilmSession savedSession = sql2oFilmSessionRepository.findById(session.getId()).get();

        assertThat(savedSession).isEqualTo(session);
    }

    @Test
    void whenFindByIdThenGetFilmSession() {
        FilmSession session = new FilmSession();
        session.setFilmId(1);
        session.setHallId(1);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now().plusHours(2));
        session.setPrice(500);

        sql2oFilmSessionRepository.save(session);
        Optional<FilmSession> foundSession = sql2oFilmSessionRepository.findById(session.getId());

        assertThat(foundSession).isPresent();
        assertThat(foundSession.get()).isEqualTo(session);
    }

    @Test
    void whenFindAllThenGetAllFilmSessions() {
        FilmSession session1 = new FilmSession();
        session1.setFilmId(1);
        session1.setHallId(1);
        session1.setStartTime(LocalDateTime.now());
        session1.setEndTime(LocalDateTime.now().plusHours(2));
        session1.setPrice(500);

        FilmSession session2 = new FilmSession();
        session2.setFilmId(1);
        session2.setHallId(1);
        session2.setStartTime(LocalDateTime.now().plusDays(1));
        session2.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        session2.setPrice(600);

        sql2oFilmSessionRepository.save(session1);
        sql2oFilmSessionRepository.save(session2);
        List<FilmSession> sessions = sql2oFilmSessionRepository.findAll();

        assertThat(sessions).hasSize(2);
        assertThat(sessions).contains(session1, session2);
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        FilmSession session = new FilmSession();
        session.setFilmId(1);
        session.setHallId(1);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now().plusHours(2));
        session.setPrice(500);

        sql2oFilmSessionRepository.save(session);
        sql2oFilmSessionRepository.deleteById(session.getId());
        Optional<FilmSession> deletedSession = sql2oFilmSessionRepository.findById(session.getId());

        assertThat(deletedSession).isEmpty();
    }

    @Test
    void whenFindByFilmIdThenGetFilmSessions() {
        FilmSession session1 = new FilmSession();
        session1.setFilmId(1);
        session1.setHallId(1);
        session1.setStartTime(LocalDateTime.now());
        session1.setEndTime(LocalDateTime.now().plusHours(2));
        session1.setPrice(500);

        FilmSession session2 = new FilmSession();
        session2.setFilmId(1);
        session2.setHallId(1);
        session2.setStartTime(LocalDateTime.now().plusDays(1));
        session2.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        session2.setPrice(600);

        sql2oFilmSessionRepository.save(session1);
        sql2oFilmSessionRepository.save(session2);
        List<FilmSession> sessionsByFilm = sql2oFilmSessionRepository.findByFilmId(1);

        assertThat(sessionsByFilm).hasSize(2);
        assertThat(sessionsByFilm).contains(session1, session2);
    }
}
