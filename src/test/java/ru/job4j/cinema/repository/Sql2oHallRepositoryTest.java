package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Hall;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.*;

class Sql2oHallRepositoryTest {

    private static Sql2oHallRepository sql2oHallRepository;

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

        sql2oHallRepository = new Sql2oHallRepository(sql2o);
    }

    @AfterEach
    public void clearHalls() {
        var halls = sql2oHallRepository.findAll();
        for (var hall : halls) {
            sql2oHallRepository.deleteById(hall.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var hall = sql2oHallRepository.save(new Hall(0, "test", 1, 1, "test"));
        var savedHall = sql2oHallRepository.findById(hall.getId()).get();
        assertThat(savedHall).usingRecursiveComparison().isEqualTo(hall);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var hall1 = sql2oHallRepository.save(new Hall(0, "test1", 1, 1, "test1"));
        var hall2 = sql2oHallRepository.save(new Hall(0, "test2", 1, 1, "test2"));
        var hall3 = sql2oHallRepository.save(new Hall(0, "test3", 1, 1, "test3"));
        var result = sql2oHallRepository.findAll();
        assertThat(result).isEqualTo(List.of(hall1, hall2, hall3));
    }

    @Test
    void whenDoNotSaveThenNothingFound() {
        assertThat(sql2oHallRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oHallRepository.findById(0)).isNotPresent();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var hall = sql2oHallRepository.save(new Hall(0, "test", 1, 1, "test"));
        var isDeleted = sql2oHallRepository.deleteById(hall.getId());
        var savedHall = sql2oHallRepository.findById(hall.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedHall).isNotPresent();
    }

    @Test
    void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oHallRepository.deleteById(0)).isFalse();
    }
}