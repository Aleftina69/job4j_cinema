package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> save(User user);

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    boolean deleteById(int id);

    Optional<User> findByEmailAndPassword(String email, String password);
}
