package ru.job4j.cinema.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SimpleHallService implements HallService {
    private final HallRepository hallRepository;

    public SimpleHallService(@Qualifier("sql2oHallRepository") HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @Override
    public Optional<Hall> findById(int id) {
        return hallRepository.findById(id);
    }

    @Override
    public Collection<Hall> findAll() {
        return hallRepository.findAll();
    }

    @Override
    public List<Integer> getRows(int hallId) {
        var hallOptional = findById(hallId);
        if (hallOptional.isEmpty()) {
            return List.of();
        }
        var hall = hallOptional.get();
        int rowCount = hall.getRowCount();
        return IntStream.rangeClosed(1, rowCount)
                .boxed()
                .collect(Collectors.toList());
    }
}
