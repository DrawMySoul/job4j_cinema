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

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

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

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(new User(0, "name", "email", "password"));
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.get().getEmail(), user.get().getPassword());
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var user1 = sql2oUserRepository.save(new User(0, "name1", "email1", "password1"));
        var user2 = sql2oUserRepository.save(new User(0, "name2", "email2", "password2"));
        var user3 = sql2oUserRepository.save(new User(0, "name3", "email3", "password3"));
        var result = sql2oUserRepository.findAll();
        assertThat(result).isEqualTo(List.of(user1.get(), user2.get(), user3.get()));
    }

    @Test
    void whenDoNotSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oUserRepository.findByEmailAndPassword("email", "password")).isNotPresent();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var user = sql2oUserRepository.save(new User(0, "name", "email", "password"));
        var isDeleted = sql2oUserRepository.deleteById(user.get().getId());
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.get().getEmail(), user.get().getPassword());
        assertThat(isDeleted).isTrue();
        assertThat(savedUser).isNotPresent();
    }

    @Test
    void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oUserRepository.deleteById(0)).isFalse();
    }
}