package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.job4j.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Sql2oTicketRepositoryTest {

    @Autowired
    private Sql2oTicketRepository sql2oTicketRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
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
                  id SERIAL PRIMARY KEY,
                  name VARCHAR(255),
                  path VARCHAR(255)
                )""");
    }

    private void createGenres() {
        jdbcTemplate.execute("""
                CREATE TABLE genres (
                  id SERIAL PRIMARY KEY,
                  name VARCHAR(255)
                )""");
    }

    private void createHalls() {
        jdbcTemplate.execute("""
                CREATE TABLE halls (
                  id SERIAL PRIMARY KEY,
                  name VARCHAR(255),
                  row_count INTEGER,
                  place_count INTEGER,
                  description VARCHAR(1024)
                )""");
    }

    private void createUsers() {
        jdbcTemplate.execute("""
                CREATE TABLE users (
                  id SERIAL PRIMARY KEY,
                  full_name VARCHAR(255),
                  email VARCHAR(255) UNIQUE,
                  password VARCHAR(255)
                )""");
    }

    private void createFilms() {
        jdbcTemplate.execute("""
                CREATE TABLE films (
                  id SERIAL PRIMARY KEY,
                  name VARCHAR(255),
                  description TEXT,
                  release_year INTEGER,
                  genre_id INTEGER REFERENCES genres(id),
                  minimal_age INTEGER,
                  duration_in_minutes INTEGER,
                  file_id INTEGER REFERENCES files(id)
                )""");
    }

    private void createFilmSessions() {
        jdbcTemplate.execute("""
                CREATE TABLE film_sessions (
                  id SERIAL PRIMARY KEY,
                  film_id INTEGER REFERENCES films(id),
                  hall_id INTEGER REFERENCES halls(id),
                  start_time TIMESTAMP,
                  end_time TIMESTAMP,
                  price INTEGER
                )""");
    }

    private void createTickets() {
        jdbcTemplate.execute("""
                CREATE TABLE tickets (
                  id SERIAL PRIMARY KEY,
                  session_id INTEGER REFERENCES film_sessions(id),
                  row_number INTEGER,
                  place_number INTEGER,
                  user_id INTEGER REFERENCES users(id)
                )""");
    }

    private void insertSeedData() {
        jdbcTemplate.update("INSERT INTO files (name, path) VALUES ('poster1', 'files/poster1.jpg')");
        jdbcTemplate.update("INSERT INTO genres (name) VALUES ('Триллер')");
        jdbcTemplate.update("INSERT INTO halls (name, row_count, place_count, description) VALUES ('Main Hall', 10, 100, 'Большой зал')");
        jdbcTemplate.update("INSERT INTO users (full_name, email, password) VALUES ('John Doe', 'john@example.com', 'password123')");
        jdbcTemplate.update("INSERT INTO users (full_name, email, password) VALUES ('Jane Smith', 'jane@example.com', 'securepass')");
        jdbcTemplate.update("INSERT INTO films (name, description, release_year, genre_id, minimal_age, duration_in_minutes, file_id) VALUES ('Film 1', 'Description 1', 2024, 1, 16, 120, 1)");
        jdbcTemplate.update("INSERT INTO film_sessions (film_id, hall_id, start_time, end_time, price) VALUES (1, 1, '2024-01-01 10:00:00', '2024-01-01 12:00:00', 500)");
    }

    @Test
    public void whenSaveTicketThenFindById() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(1);
        ticket.setRowNumber(1);
        ticket.setPlaceNumber(1);
        ticket.setUserId(1);

        Optional<Ticket> savedTicketOptional = sql2oTicketRepository.save(ticket);
        assertThat(savedTicketOptional).isPresent();

        Ticket savedTicket = savedTicketOptional.get();
        Optional<Ticket> foundTicket = sql2oTicketRepository.findById(savedTicket.getId());

        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getSessionId()).isEqualTo(1);
        assertThat(foundTicket.get().getRowNumber()).isEqualTo(1);
        assertThat(foundTicket.get().getPlaceNumber()).isEqualTo(1);
        assertThat(foundTicket.get().getUserId()).isEqualTo(1);
    }

    @Test
    public void whenSaveTicketThenFindAll() {
        Ticket ticket1 = new Ticket();
        ticket1.setSessionId(1);
        ticket1.setRowNumber(1);
        ticket1.setPlaceNumber(1);
        ticket1.setUserId(1);

        Ticket ticket2 = new Ticket();
        ticket2.setSessionId(1);
        ticket2.setRowNumber(2);
        ticket2.setPlaceNumber(2);
        ticket2.setUserId(1);

        Optional<Ticket> saved1 = sql2oTicketRepository.save(ticket1);
        Optional<Ticket> saved2 = sql2oTicketRepository.save(ticket2);
        assertThat(saved1).isPresent();
        assertThat(saved2).isPresent();

        List<Ticket> tickets = sql2oTicketRepository.findAll();
        assertThat(tickets.size()).isEqualTo(2);
    }

    @Test
    public void whenDeleteTicketThenNotFound() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(1);
        ticket.setRowNumber(1);
        ticket.setPlaceNumber(1);
        ticket.setUserId(1);

        Optional<Ticket> savedTicketOptional = sql2oTicketRepository.save(ticket);
        assertThat(savedTicketOptional).isPresent();

        Ticket savedTicket = savedTicketOptional.get();
        boolean isDeleted = sql2oTicketRepository.deleteById(savedTicket.getId());
        assertThat(isDeleted).isTrue();

        Optional<Ticket> foundTicket = sql2oTicketRepository.findById(savedTicket.getId());
        assertThat(foundTicket).isEmpty();
    }

    @Test
    public void whenFindTicketsBySessionId() {
        Ticket ticket1 = new Ticket();
        ticket1.setSessionId(1);
        ticket1.setRowNumber(1);
        ticket1.setPlaceNumber(1);
        ticket1.setUserId(1);

        Ticket ticket2 = new Ticket();
        ticket2.setSessionId(1);
        ticket2.setRowNumber(2);
        ticket2.setPlaceNumber(2);
        ticket2.setUserId(1);

        sql2oTicketRepository.save(ticket1);
        sql2oTicketRepository.save(ticket2);

        List<Ticket> tickets = sql2oTicketRepository.findTicketsBySessionId(1);
        assertThat(tickets.size()).isEqualTo(2);
    }

    @Test
    public void whenFindBySessionIdAndRowAndPlace() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(1);
        ticket.setRowNumber(1);
        ticket.setPlaceNumber(1);
        ticket.setUserId(1);

        sql2oTicketRepository.save(ticket);

        Optional<Ticket> foundTicket = sql2oTicketRepository.findBySessionIdAndRowAndPlace(1, 1, 1);
        assertThat(foundTicket).isPresent();
        assertThat(foundTicket.get().getRowNumber()).isEqualTo(1);
        assertThat(foundTicket.get().getPlaceNumber()).isEqualTo(1);
    }
}
