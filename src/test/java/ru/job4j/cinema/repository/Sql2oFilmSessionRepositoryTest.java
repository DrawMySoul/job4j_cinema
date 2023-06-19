package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.*;

import java.io.InputStream;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oFilmSessionRepositoryTest {

    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static Sql2oGenreRepository sql2oGenreRepository;

    private static Sql2oHallRepository sql2oHallRepository;

    private static Sql2oFilmRepository sql2oFilmRepository;

    private static File file;

    private static Genre genre;

    private static Hall hall;

    private static Film film;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (InputStream inputStream = Sql2oFilmSessionRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        sql2oHallRepository = new Sql2oHallRepository(sql2o);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);

        genre = new Genre(1, "test");
        sql2oGenreRepository.save(genre);

        hall = new Hall(1, "test", 1, 1, "test");
        sql2oHallRepository.save(hall);

        film = new Film(1, "test", "test", 1, genre.getId(), 1, 1, file.getId());
        sql2oFilmRepository.save(film);
    }

    @AfterAll
    public static void deleteFiles() {
        sql2oFilmRepository.deleteById(film.getId());
        sql2oGenreRepository.deleteById(genre.getId());
        sql2oHallRepository.deleteById(hall.getId());
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clearFilmSessions() {
        var filmSessions = sql2oFilmSessionRepository.findAll();
        for (var filmSession : filmSessions) {
            sql2oFilmSessionRepository.deleteById(filmSession.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var startTime = now().truncatedTo(ChronoUnit.MINUTES);
        var endTime = now().plusMinutes(60).truncatedTo(ChronoUnit.MINUTES);
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(0, film.getId(), hall.getId(), startTime, endTime, 1));
        var savedFilmSession = sql2oFilmSessionRepository.findById(filmSession.getId()).get();
        assertThat(savedFilmSession).usingRecursiveComparison().isEqualTo(filmSession);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var startTime = now().truncatedTo(ChronoUnit.MINUTES);
        var endTime = now().plusMinutes(60).truncatedTo(ChronoUnit.MINUTES);
        var filmSession1 = sql2oFilmSessionRepository.save(new FilmSession(0, film.getId(), hall.getId(), startTime, endTime, 1));
        var filmSession2 = sql2oFilmSessionRepository.save(new FilmSession(0, film.getId(), hall.getId(), startTime, endTime, 1));
        var filmSession3 = sql2oFilmSessionRepository.save(new FilmSession(0, film.getId(), hall.getId(), startTime, endTime, 1));
        var result = sql2oFilmSessionRepository.findAll();
        assertThat(result).isEqualTo(List.of(filmSession1, filmSession2, filmSession3));
    }

    @Test
    void whenDoNotSaveThenNothingFound() {
        assertThat(sql2oFilmSessionRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oFilmSessionRepository.findById(0)).isNotPresent();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var startTime = now().truncatedTo(ChronoUnit.MINUTES);
        var endTime = now().plusMinutes(60).truncatedTo(ChronoUnit.MINUTES);
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(0, film.getId(), hall.getId(), startTime, endTime, 1));
        var isDeleted = sql2oFilmSessionRepository.deleteById(filmSession.getId());
        var savedFilmSession = sql2oFilmSessionRepository.findById(filmSession.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedFilmSession).isNotPresent();
    }

    @Test
    void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oFilmSessionRepository.deleteById(0)).isFalse();
    }
}