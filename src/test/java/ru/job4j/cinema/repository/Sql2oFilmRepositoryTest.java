package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oFilmRepositoryTest {

    private static Sql2oFilmRepository sql2oFilmRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static Sql2oGenreRepository sql2oGenreRepository;

    private static File file;

    private static Genre genre;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (InputStream inputStream = Sql2oFilmRepository.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);

        genre = new Genre(1, "test");
        sql2oGenreRepository.save(genre);
    }

    @AfterAll
    public static void deleteFiles() {
        sql2oFileRepository.deleteById(file.getId());
        sql2oGenreRepository.deleteById(genre.getId());
    }

    @AfterEach
    public void clearFilms() {
        var films = sql2oFilmRepository.findAll();
        for (var film : films) {
            sql2oFilmRepository.deleteById(film.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var film = sql2oFilmRepository.save(new Film(0, "name", "description", 1, genre.getId(), 1, 1, file.getId()));
        var savedFilm = sql2oFilmRepository.findById(film.getId()).get();
        assertThat(savedFilm).usingRecursiveComparison().isEqualTo(film);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var film1 = sql2oFilmRepository.save(new Film(0, "name1", "description1", 1, genre.getId(), 1, 1, file.getId()));
        var film2 = sql2oFilmRepository.save(new Film(0, "name2", "description2", 1, genre.getId(), 1, 1, file.getId()));
        var film3 = sql2oFilmRepository.save(new Film(0, "name3", "description3", 1, genre.getId(), 1, 1, file.getId()));
        var result = sql2oFilmRepository.findAll();
        assertThat(result).isEqualTo(List.of(film1, film2, film3));
    }

    @Test
    void whenDoNotSaveThenNothingFound() {
        assertThat(sql2oFilmRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oFilmRepository.findById(0)).isNotPresent();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var film = sql2oFilmRepository.save(new Film(0, "name", "description", 1, genre.getId(), 1, 1, file.getId()));
        var isDeleted = sql2oFilmRepository.deleteById(film.getId());
        var savedFilm = sql2oFilmRepository.findById(film.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedFilm).isNotPresent();
    }

    @Test
    void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oFilmRepository.deleteById(0)).isFalse();
    }
}