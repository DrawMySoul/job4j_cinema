package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;

@ThreadSafe
@Service
public class SimpleHallService implements HallService {

    private final HallRepository hallRepository;

    public SimpleHallService(HallRepository hallRepository) {
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
    public Collection<Integer> getRowsByHallId(int id) {
        return IntStream.rangeClosed(1, findById(id).get().getRowCount())
            .boxed()
            .toList();
    }

    @Override
    public Collection<Integer> getPlacesByHallId(int id) {
        return IntStream.rangeClosed(1, findById(id).get().getPlaceCount())
            .boxed()
            .toList();
    }
}
