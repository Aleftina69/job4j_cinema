package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.RegistrationForm;
import ru.job4j.cinema.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> register(RegistrationForm form);

    Optional<User> findById(int id);

    List<User> findAll();

    boolean deleteById(int id);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email, String password);
}
