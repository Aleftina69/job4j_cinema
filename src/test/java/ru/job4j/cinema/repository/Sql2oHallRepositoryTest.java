package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Hall;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Sql2oHallRepositoryTest {

    private Sql2oHallRepository sql2oHallRepository;
    private Sql2o sql2o;

    @BeforeEach
    void setUp() {
        String dbName = "testdb_" + this.getClass().getSimpleName();
        String jdbcUrl = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        sql2o = new Sql2o("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        sql2oHallRepository = new Sql2oHallRepository(sql2o);

        try (Connection connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS halls").executeUpdate();

            connection.createQuery("""
                    CREATE TABLE halls (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        row_count INT NOT NULL,
                        place_count INT NOT NULL,
                        description TEXT
                    )
                    """).executeUpdate();

            connection.createQuery("INSERT INTO halls (name, row_count, place_count, description) VALUES (:name, :rowCount, :placeCount, :description)")
                    .addParameter("name", "Main Hall")
                    .addParameter("rowCount", 10)
                    .addParameter("placeCount", 100)
                    .addParameter("description", "Large hall for premieres")
                    .executeUpdate();
            connection.createQuery("INSERT INTO halls (name, row_count, place_count, description) VALUES (:name, :rowCount, :placeCount, :description)")
                    .addParameter("name", "Small Hall")
                    .addParameter("rowCount", 5)
                    .addParameter("placeCount", 50)
                    .addParameter("description", "Intimate hall for small events")
                    .executeUpdate();
        }
    }

    @AfterEach
    void tearDown() {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM halls").executeUpdate();
            connection.createQuery("DROP TABLE IF EXISTS halls").executeUpdate();
        }
    }

    @Test
    void whenSaveThenGetSame() {
        Hall hall = new Hall();
        hall.setName("VIP Hall");
        hall.setRowCount(8);
        hall.setPlaceCount(80);
        hall.setDescription("Exclusive VIP experience");

        Hall savedHall = sql2oHallRepository.save(hall);
        Hall foundHall = sql2oHallRepository.findById(savedHall.getId()).get();

        assertThat(foundHall.getName()).isEqualTo("VIP Hall");
        assertThat(foundHall.getRowCount()).isEqualTo(8);
        assertThat(foundHall.getPlaceCount()).isEqualTo(80);
        assertThat(foundHall.getDescription()).isEqualTo("Exclusive VIP experience");
    }

    @Test
    void whenFindByIdThenGetHall() {
        Hall hall = new Hall();
        hall.setName("Outdoor Hall");
        hall.setRowCount(12);
        hall.setPlaceCount(120);
        hall.setDescription("Open-air venue");

        Hall savedHall = sql2oHallRepository.save(hall);
        Optional<Hall> foundHall = sql2oHallRepository.findById(savedHall.getId());

        assertThat(foundHall).isPresent();
        assertThat(foundHall.get().getName()).isEqualTo("Outdoor Hall");
        assertThat(foundHall.get().getRowCount()).isEqualTo(12);
        assertThat(foundHall.get().getPlaceCount()).isEqualTo(120);
        assertThat(foundHall.get().getDescription()).isEqualTo("Open-air venue");
    }

    @Test
    void whenFindAllThenGetAllHalls() {
        List<Hall> halls = sql2oHallRepository.findAll();

        assertThat(halls).hasSizeGreaterThanOrEqualTo(2);
        assertThat(halls.stream().anyMatch(h -> "Main Hall".equals(h.getName()))).isTrue();
        assertThat(halls.stream().anyMatch(h -> "Small Hall".equals(h.getName()))).isTrue();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        Hall hall = new Hall();
        hall.setName("Temporary Hall");
        hall.setRowCount(6);
        hall.setPlaceCount(60);
        hall.setDescription("Temporary setup");

        Hall savedHall = sql2oHallRepository.save(hall);
        sql2oHallRepository.deleteById(savedHall.getId());
        Optional<Hall> deletedHall = sql2oHallRepository.findById(savedHall.getId());

        assertThat(deletedHall).isEmpty();
    }
}
