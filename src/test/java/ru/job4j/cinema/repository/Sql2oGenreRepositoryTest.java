package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.*;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oGenreRepositoryTest {

    private static Sql2oGenreRepository sql2oGenreRepository;

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

        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
    }

    @AfterEach
    public void clearGenres() {
        var genres = sql2oGenreRepository.findAll();
        for (var genre : genres) {
            sql2oGenreRepository.deleteById(genre.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var genre = sql2oGenreRepository.save(new Genre(0, "test"));
        var savedGenre = sql2oGenreRepository.findById(genre.getId()).get();
        assertThat(savedGenre).usingRecursiveComparison().isEqualTo(genre);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var genre1 = sql2oGenreRepository.save(new Genre(0, "test1"));
        var genre2 = sql2oGenreRepository.save(new Genre(0, "test2"));
        var genre3 = sql2oGenreRepository.save(new Genre(0, "test3"));
        var result = sql2oGenreRepository.findAll();
        assertThat(result).isEqualTo(List.of(genre1, genre2, genre3));
    }

    @Test
    void whenDoNotSaveThenNothingFound() {
        assertThat(sql2oGenreRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oGenreRepository.findById(0)).isNotPresent();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var genre = sql2oGenreRepository.save(new Genre(0, "test"));
        var isDeleted = sql2oGenreRepository.deleteById(genre.getId());
        var savedGenre = sql2oGenreRepository.findById(genre.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedGenre).isNotPresent();
    }

    @Test
    void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oGenreRepository.deleteById(0)).isFalse();
    }
}