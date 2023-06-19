package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilmSessionControllerTest {

    private FilmSessionController filmSessionController;

    private FilmSessionService filmSessionService;

    private HallService hallService;

    private FilmService filmService;

    private final LocalDateTime startTime = LocalDateTime.now();

    private final LocalDateTime endTime = LocalDateTime.now().plusMinutes(60);

    @BeforeEach
    public void initService() {
        filmSessionService = mock(FilmSessionService.class);
        hallService = mock(HallService.class);
        filmService = mock(FilmService.class);
        filmSessionController = new FilmSessionController(filmSessionService, hallService, filmService);
    }

    @Test
    void whenRequestFilmSessionsListPageThenGetPageWithFilmSessions() {
        var filmSessionDTO1 = new FilmSessionDTO(1, "film1", "hall1", "description1", startTime, endTime);
        var filmSessionDTO2 = new FilmSessionDTO(2, "film2", "hall2", "description2", startTime, endTime);
        var expectedFilmSessions = List.of(filmSessionDTO1, filmSessionDTO2);
        when(filmSessionService.findAll()).thenReturn(expectedFilmSessions);

        var model = new ConcurrentModel();
        var view = filmSessionController.getAll(model);
        var actualFilmSessions = model.getAttribute("filmSessions");

        assertThat(view).isEqualTo("filmSessions/list");
        assertThat(actualFilmSessions).isEqualTo(expectedFilmSessions);
    }

    @Test
    void whenRequestBuyTicketPageThenGetBuyTicketPage() {
        var expectedFilmSession = new FilmSession(1, 1, 1, startTime, endTime, 1);
        var expectedFilm = new Film(1, "name", "description", 1, 1, 1, 1, 1);
        var expectedRows = List.of(1, 2, 3, 4);
        var expectedPalaces = List.of(1, 2, 3, 4);
        when(filmSessionService.findById(anyInt())).thenReturn(Optional.of(expectedFilmSession));
        when(filmService.findById(anyInt())).thenReturn(Optional.of(expectedFilm));
        when(hallService.getRowsByHallId(anyInt())).thenReturn(expectedRows);
        when(hallService.getPlacesByHallId(anyInt())).thenReturn(expectedPalaces);

        var model = new ConcurrentModel();
        var view = filmSessionController.getById(model, 1);
        var actualFilmSession = model.getAttribute("filmSession");
        var actualFilm = model.getAttribute("film");
        var actualRows = model.getAttribute("rows");
        var actualPlaces = model.getAttribute("places");

        assertThat(view).isEqualTo("tickets/buy");
        assertThat(actualFilmSession).isEqualTo(expectedFilmSession);
        assertThat(actualFilm).isEqualTo(expectedFilm);
        assertThat(actualRows).isEqualTo(expectedRows);
        assertThat(actualPlaces).isEqualTo(expectedPalaces);
    }

    @Test
    void whenRequestWrongBuyTicketsPageThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Сеанс с указанным идентификатором не найден");
        when(filmSessionService.findById(anyInt())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = filmSessionController.getById(model, 1);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }
}