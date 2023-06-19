package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.TicketService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

class TicketControllerTest {

    private TicketController ticketController;

    private TicketService ticketService;

    @BeforeEach
    public void initService() {
        ticketService = mock(TicketService.class);
        ticketController = new TicketController(ticketService);
    }

    @Test
    void whenBuyTicketThenGetSuccessPage() {
        var ticket = new Ticket(1, 1, 1, 1, 1);
        var expectedMessage = "Ваш сеанс 1, место 1, ряд 1. Приятного просмотра!";
        when(ticketService.save(any())).thenReturn(Optional.of(ticket));

        var model = new ConcurrentModel();
        var view = ticketController.buy(ticket, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("tickets/success");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void whenBuyTicketThenGetErrorPageWithMessage() {
        var ticket = new Ticket(1, 1, 1, 1, 1);
        var expectedMessage = """
            Не удалось приобрести билет на заданное место.
            Вероятно оно уже занято.
            Перейдите на страницу бронирования билетов и попробуйте снова.
            """;
        when(ticketService.save(any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = ticketController.buy(ticket, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}