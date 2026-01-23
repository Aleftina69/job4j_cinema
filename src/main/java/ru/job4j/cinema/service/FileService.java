package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FileDTO;

import java.util.Optional;

public interface FileService {

    Optional<FileDTO> findById(int id);
}

