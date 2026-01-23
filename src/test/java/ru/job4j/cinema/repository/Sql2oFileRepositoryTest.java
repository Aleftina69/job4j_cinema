package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.File;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("test")
class Sql2oFileRepositoryTest {

    private Sql2oFileRepository fileRepository;
    private Sql2o sql2o;

    @BeforeEach
    public void setUp() {
        String dbName = "testdb_" + this.getClass().getSimpleName();
        String jdbcUrl = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        sql2o = new Sql2o(jdbcUrl, "sa", "");
        fileRepository = new Sql2oFileRepository(sql2o);
        try (Connection conn = sql2o.open()) {
            dropTables(conn);
            createHallsTable(conn);
            createGenresTable(conn);
            createFilmsTable(conn);
            createFilesTable(conn);
            createFilmSessionsTable(conn);
            createUsersTable(conn);
            createTicketsTable(conn);
        }
    }

    private void dropTables(Connection conn) {
        conn.createQuery("""
                DROP TABLE IF EXISTS halls, tickets, film_sessions, files, films, genres, users CASCADE;
                """).executeUpdate();
    }

    private void createHallsTable(Connection conn) {
        conn.createQuery("""
                CREATE TABLE halls (
                 id SERIAL PRIMARY KEY,
                 name VARCHAR(255)
                );
                """).executeUpdate();
    }

    private void createGenresTable(Connection conn) {
        conn.createQuery("""
                CREATE TABLE genres (
                 id SERIAL PRIMARY KEY,
                 name VARCHAR(255)
                );
                """).executeUpdate();
    }

    private void createFilmsTable(Connection conn) {
        conn.createQuery("""
                CREATE TABLE films (
                  id SERIAL PRIMARY KEY,
                  name VARCHAR(255),
                  genre_id INTEGER
                );
                """).executeUpdate();
    }

    private void createFilesTable(Connection conn) {
        conn.createQuery("""
                CREATE TABLE files (
                  id SERIAL PRIMARY KEY,
                  name VARCHAR(255),
                  path VARCHAR(255)
                );
                """).executeUpdate();
    }

    private void createFilmSessionsTable(Connection conn) {
        conn.createQuery("""
                CREATE TABLE film_sessions (
                  id SERIAL PRIMARY KEY,
                  film_id INTEGER REFERENCES films(id),
                  show_time TIMESTAMP
                );
                """).executeUpdate();
    }

    private void createUsersTable(Connection conn) {
        conn.createQuery("""
                CREATE TABLE users (
                  id SERIAL PRIMARY KEY,
                  username VARCHAR(255)
                );
                """).executeUpdate();
    }

    private void createTicketsTable(Connection conn) {
        conn.createQuery("""
                CREATE TABLE tickets (
                  id SERIAL PRIMARY KEY,
                  session_id INTEGER REFERENCES film_sessions(id),
                  user_id INTEGER REFERENCES users(id)
                );
                """).executeUpdate();
    }

    @AfterEach
    public void tearDown() {
        try (Connection conn = sql2o.open()) {
            deleteFromTable(conn, "tickets");
            deleteFromTable(conn, "users");
            deleteFromTable(conn, "film_sessions");
            deleteFromTable(conn, "halls");
            deleteFromTable(conn, "films");
            deleteFromTable(conn, "files");
            deleteFromTable(conn, "genres");
        }
    }

    private void deleteFromTable(Connection conn, String tableName) {
        String sql = String.format("DELETE FROM %s", tableName);
        conn.createQuery(sql).executeUpdate();
    }

    @Test
    void whenSaveFileThenReturnFileWithId() {
        var file = new File();
        file.setName("test.jpg");
        file.setPath("/path/to/test.jpg");

        var savedFile = fileRepository.save(file);

        assertThat(savedFile.getId()).isNotNull();
        assertThat(savedFile.getName()).isEqualTo("test.jpg");
        assertThat(savedFile.getPath()).isEqualTo("/path/to/test.jpg");

        try (Connection connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM files WHERE id = :id");
            var fetchedFile = query.addParameter("id", savedFile.getId()).executeAndFetchFirst(File.class);
            assertThat(fetchedFile).isNotNull();
            assertThat(fetchedFile.getName()).isEqualTo("test.jpg");
        }
    }

    @Test
    void whenFindByIdExistingThenReturnFile() {
        var file = new File();
        file.setName("existing.jpg");
        file.setPath("/path/to/existing.jpg");
        int id;
        try (Connection connection = sql2o.open()) {
            var sql = "INSERT INTO files(name, path) VALUES (:name, :path)";
            var query = connection.createQuery(sql, true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath());
            id = query.executeUpdate().getKey(Integer.class);
        }

        var foundFile = fileRepository.findById(id);

        assertThat(foundFile).isPresent();
        assertThat(foundFile.get().getId()).isEqualTo(id);
        assertThat(foundFile.get().getName()).isEqualTo("existing.jpg");
        assertThat(foundFile.get().getPath()).isEqualTo("/path/to/existing.jpg");
    }

    @Test
    void whenFindByIdNonExistingThenReturnEmpty() {
        var foundFile = fileRepository.findById(999);
        assertThat(foundFile).isEmpty();
    }

    @Test
    void whenDeleteByIdExistingThenFileRemoved() {
        var file = new File();
        file.setName("to_delete.jpg");
        file.setPath("/path/to/to_delete.jpg");
        int id;
        try (Connection connection = sql2o.open()) {
            var sql = "INSERT INTO files(name, path) VALUES (:name, :path)";
            var query = connection.createQuery(sql, true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath());
            id = query.executeUpdate().getKey(Integer.class);
        }

        fileRepository.deleteById(id);

        try (Connection connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM files WHERE id = :id");
            var fetchedFile = query.addParameter("id", id).executeAndFetchFirst(File.class);
            assertThat(fetchedFile).isNull();
        }
    }

    @Test
    void whenDeleteByIdNonExistingThenNoException() {
        assertThatCode(() -> fileRepository.deleteById(999)).doesNotThrowAnyException();
    }

    @Test
    void whenFindAllThenReturnAllFiles() {

        var file1 = new File();
        file1.setName("file1.jpg");
        file1.setPath("/path/to/file1.jpg");
        var file2 = new File();
        file2.setName("file2.jpg");
        file2.setPath("/path/to/file2.jpg");

        fileRepository.save(file1);
        fileRepository.save(file2);

        Collection<File> files = fileRepository.findAll();

        assertThat(files).hasSize(2);
        assertThat(files).extracting(File::getName).contains("file1.jpg", "file2.jpg");
    }

    @Test
    void whenFindAllEmptyThenReturnEmptyCollection() {
        Collection<File> files = fileRepository.findAll();
        assertThat(files).isEmpty();
    }
}
