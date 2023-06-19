package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.*;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.*;

class Sql2oTicketRepositoryTest {

    private static Sql2oTicketRepository sql2oTicketRepository;

    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;

    private static Sql2oFilmRepository sql2oFilmRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static Sql2oGenreRepository sql2oGenreRepository;

    private static Sql2oHallRepository sql2oHallRepository;

    private static Sql2oUserRepository sql2oUserRepository;

    private static File file;

    private static Genre genre;

    private static Film film;

    private static Hall hall;

    private static FilmSession filmSession;

    private static User user;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oTicketRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oTicketRepository = new Sql2oTicketRepository(sql2o);
        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        sql2oHallRepository = new Sql2oHallRepository(sql2o);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);

        genre = new Genre(1, "test");
        sql2oGenreRepository.save(genre);

        film = new Film(1, "test", "test", 1, genre.getId(), 1, 1, file.getId());
        sql2oFilmRepository.save(film);

        hall = new Hall(1, "test1", 10, 10, "test1");
        sql2oHallRepository.save(hall);

        user = new User(1, "name", "email", "password");
        sql2oUserRepository.save(user);

        filmSession = new FilmSession(1, film.getId(), hall.getId(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), 1);
        sql2oFilmSessionRepository.save(filmSession);
    }

    @AfterAll
    public static void deleteFiles() {
        sql2oFilmRepository.deleteById(film.getId());
        sql2oGenreRepository.deleteById(genre.getId());
        sql2oHallRepository.deleteById(hall.getId());
        sql2oFileRepository.deleteById(file.getId());
        sql2oFilmSessionRepository.deleteById(filmSession.getId());
        sql2oUserRepository.deleteById(user.getId());
    }

    @AfterEach
    public void clearTickets() {
        var tickets = sql2oTicketRepository.findAll();
        for (var ticket : tickets) {
            sql2oTicketRepository.deleteById(ticket.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var ticket = sql2oTicketRepository.save(new Ticket(0, filmSession.getId(), 1, 1, user.getId()));
        var savedTicket = sql2oTicketRepository.findById(ticket.get().getId()).get();
        assertThat(savedTicket).usingRecursiveComparison().isEqualTo(savedTicket);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var ticket1 = sql2oTicketRepository.save(new Ticket(0, filmSession.getId(), 1, 1, user.getId()));
        var ticket2 = sql2oTicketRepository.save(new Ticket(0, filmSession.getId(), 2, 2, user.getId()));
        var ticket3 = sql2oTicketRepository.save(new Ticket(0, filmSession.getId(), 3, 3, user.getId()));
        var result = sql2oTicketRepository.findAll();
        assertThat(result).isEqualTo(List.of(ticket1.get(), ticket2.get(), ticket3.get()));
    }

    @Test
    void whenDoNotSaveThenNothingFound() {
        assertThat(sql2oTicketRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oTicketRepository.findById(0)).isNotPresent();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var ticket = sql2oTicketRepository.save(new Ticket(0, filmSession.getId(), 1, 1, user.getId()));
        var isDeleted = sql2oTicketRepository.deleteById(ticket.get().getId());
        var savedTicket = sql2oTicketRepository.findById(ticket.get().getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedTicket).isNotPresent();
    }

    @Test
    void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oTicketRepository.deleteById(0)).isFalse();
    }
}