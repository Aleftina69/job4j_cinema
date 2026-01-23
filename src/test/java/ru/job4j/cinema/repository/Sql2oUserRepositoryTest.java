package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.job4j.cinema.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Sql2oUserRepositoryTest {

    @Autowired
    private Sql2oUserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        recreateSchema();
        insertSeedData();
    }

    private void recreateSchema() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS tickets");
        jdbcTemplate.execute("DROP TABLE IF EXISTS film_sessions");
        jdbcTemplate.execute("DROP TABLE IF EXISTS films");
        jdbcTemplate.execute("DROP TABLE IF EXISTS genres");
        jdbcTemplate.execute("DROP TABLE IF EXISTS halls");
        jdbcTemplate.execute("DROP TABLE IF EXISTS files");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");

        createFiles();
        createGenres();
        createHalls();
        createUsers();
        createFilms();
        createFilmSessions();
        createTickets();
    }

    private void createFiles() {
        jdbcTemplate.execute("""
            CREATE TABLE files (
              id INT AUTO_INCREMENT PRIMARY KEY,
              name VARCHAR(255),
              path VARCHAR(255)
            )
        """);
    }

    private void createGenres() {
        jdbcTemplate.execute("""
            CREATE TABLE genres (
              id INT AUTO_INCREMENT PRIMARY KEY,
              name VARCHAR(255)
            )
        """);
    }

    private void createHalls() {
        jdbcTemplate.execute("""
            CREATE TABLE halls (
              id INT AUTO_INCREMENT PRIMARY KEY,
              name VARCHAR(255),
              row_count INT,
              place_count INT,
              description VARCHAR(1024)
            )
        """);
    }

    private void createUsers() {
        jdbcTemplate.execute("""
            CREATE TABLE users (
              id INT AUTO_INCREMENT PRIMARY KEY,
              full_name VARCHAR(255),
              email VARCHAR(255) UNIQUE,
              password VARCHAR(255)
            )
        """);
    }

    private void createFilms() {
        jdbcTemplate.execute("""
            CREATE TABLE films (
              id INT AUTO_INCREMENT PRIMARY KEY,
              name VARCHAR(255),
              description VARCHAR(1024),
              release_year INT,
              genre_id INT,
              minimal_age INT,
              duration_in_minutes INT,
              file_id INT,
              FOREIGN KEY (genre_id) REFERENCES genres(id),
              FOREIGN KEY (file_id) REFERENCES files(id)
            )
        """);
    }

    private void createFilmSessions() {
        jdbcTemplate.execute("""
            CREATE TABLE film_sessions (
              id INT AUTO_INCREMENT PRIMARY KEY,
              film_id INT,
              hall_id INT,
              start_time TIMESTAMP,
              end_time TIMESTAMP,
              price INT,
              FOREIGN KEY (film_id) REFERENCES films(id),
              FOREIGN KEY (hall_id) REFERENCES halls(id)
            )
        """);
    }

    private void createTickets() {
        jdbcTemplate.execute("""
            CREATE TABLE tickets (
              id INT AUTO_INCREMENT PRIMARY KEY,
              session_id INT,
              user_id INT,
              FOREIGN KEY (session_id) REFERENCES film_sessions(id),
              FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """);
    }

    private void insertSeedData() {
        jdbcTemplate.update("INSERT INTO files (name, path) VALUES ('poster1', 'files/poster1.jpg')");
        jdbcTemplate.update("INSERT INTO genres (name) VALUES ('Триллер')");
        jdbcTemplate.update("INSERT INTO halls (name, row_count, place_count, description) VALUES ('Main Hall', 10, 100, 'Большой зал')");
        jdbcTemplate.update("INSERT INTO films (name, description, release_year, genre_id, minimal_age, duration_in_minutes, file_id) VALUES ('Film 1', 'Description 1', 2024, 1, 16, 120, 1)");
        jdbcTemplate.update("INSERT INTO film_sessions (film_id, hall_id, start_time, end_time, price) VALUES (1, 1, '2024-01-01 10:00:00', '2024-01-01 12:00:00', 500)");
        jdbcTemplate.update("INSERT INTO users (full_name, email, password) VALUES ('John Doe', 'john@example.com', 'password123')");
        jdbcTemplate.update("INSERT INTO users (full_name, email, password) VALUES ('Jane Smith', 'jane@example.com', 'securepass')");
    }

    @Test
    void whenSaveUserThenSuccess() {
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("pass");

        Optional<User> saved = userRepository.save(user);

        assertThat(saved).isPresent();
        assertThat(saved.get().getId()).isPositive();
    }

    @Test
    void whenDuplicateEmailThenEmpty() {
        User u1 = new User();
        u1.setFullName("A");
        u1.setEmail("dup@test.com");
        u1.setPassword("1");
        userRepository.save(u1);

        User u2 = new User();
        u2.setFullName("B");
        u2.setEmail("dup@test.com");
        u2.setPassword("2");

        assertThat(userRepository.save(u2)).isEmpty();
    }

    @Test
    void whenFindByIdThenSuccess() {
        Optional<User> user = userRepository.findById(1);
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void whenFindAllThenTwoUsers() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    void whenFindByEmailAndPasswordThenSuccess() {
        Optional<User> user = userRepository.findByEmailAndPassword("john@example.com", "password123");
        assertThat(user).isPresent();
    }
}
