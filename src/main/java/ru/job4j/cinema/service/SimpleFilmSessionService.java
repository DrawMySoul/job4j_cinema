package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.repository.FilmSessionRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleFilmSessionService implements FilmSessionService {

    private final FilmSessionRepository filmSessionRepository;

    private final FilmService filmService;

    private final HallService hallService;

    public SimpleFilmSessionService(FilmSessionRepository filmSessionRepository, FilmService filmService, HallService hallService) {
        this.filmSessionRepository = filmSessionRepository;
        this.filmService = filmService;
        this.hallService = hallService;
    }


    @Override
    public Optional<FilmSession> findById(int id) {
        return filmSessionRepository.findById(id);
    }

    @Override
    public Collection<FilmSessionDTO> findAll() {
        return filmSessionRepository.findAll().stream()
            .map(filmSession -> new FilmSessionDTO(
                filmSession.getId(),
                filmService.findById(filmSession.getFilmId()).get().getName(),
                hallService.findById(filmSession.getHallId()).get().getName(),
                hallService.findById(filmSession.getHallId()).get().getDescription(),
                filmSession.getStartTime(),
                filmSession.getEndTime())
            ).toList();
    }
}
