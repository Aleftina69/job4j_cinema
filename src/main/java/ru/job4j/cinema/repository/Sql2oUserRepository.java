package ru.job4j.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.map.UserMapper;
import ru.job4j.cinema.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Sql2oUserRepository.class);

    private final Sql2o sql2o;
    private final UserMapper userMapper;

    public Sql2oUserRepository(Sql2o sql2o, UserMapper userMapper) {
        this.sql2o = sql2o;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> save(User user) {
        try (var connection = sql2o.open()) {
            var sql = """
                INSERT INTO users(full_name, email, password)
                VALUES (:fullName, :email, :password)
                """;
            Integer id = connection.createQuery(sql, true)
                    .addParameter("fullName", user.getFullName())
                    .addParameter("email", user.getEmail())
                    .addParameter("password", user.getPassword())
                    .executeUpdate()
                    .getKey(Integer.class);

            if (id != null) {
                user.setId(id);
                return Optional.of(user);
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users WHERE id = :id")
                    .addParameter("id", id);
            var user = query.executeAndFetchFirst(userMapper);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users WHERE email = :email")
                    .addParameter("email", email);
            var user = query.executeAndFetchFirst(userMapper);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users");
            return query.executeAndFetch(userMapper);
        } catch (Exception e) {
            LOG.error("Error", e);
            return List.of();
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM users WHERE id = :id")
                    .addParameter("id", id);
            var affectedRows = query.executeUpdate().getResult();
            return affectedRows > 0;
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return false;
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users WHERE email = :email AND password = :password")
                    .addParameter("email", email)
                    .addParameter("password", password);
            var user = query.executeAndFetchFirst(userMapper);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return Optional.empty();
    }
}

