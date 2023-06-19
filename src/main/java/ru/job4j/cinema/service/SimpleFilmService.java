package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDTO;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.repository.FilmRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleFilmService implements FilmService {

    private final FilmRepository filmRepository;

    private final GenreService genreService;

    public SimpleFilmService(FilmRepository filmRepository, GenreService genreService) {
        this.filmRepository = filmRepository;
        this.genreService = genreService;
    }

    @Override
    public Optional<Film> findById(int id) {
        return filmRepository.findById(id);
    }

    @Override
    public Collection<FilmDTO> findAll() {
        return filmRepository.findAll().stream()
            .map(film -> new FilmDTO(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getYear(),
                film.getMinAge(),
                film.getDurationInMinutes(),
                genreService.findById(film.getGenreId()).get().getName(),
                film.getFileId())
            ).toList();
    }
}
