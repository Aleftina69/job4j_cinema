package ru.job4j.cinema.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.RegistrationForm;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service

public class SimpleUserService implements UserService {
    private final UserRepository userRepository;

    public SimpleUserService(@Qualifier("sql2oUserRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> register(RegistrationForm form) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            return Optional.empty();
        }
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setFullName(form.getFullName());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean deleteById(int id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}
