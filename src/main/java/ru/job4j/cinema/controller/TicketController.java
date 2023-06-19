package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.TicketService;

@ThreadSafe
@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @PostMapping("/buy")
    public String buy(@ModelAttribute Ticket ticket, Model model) {
        var ticketOptional = ticketService.save(ticket);
        if (ticketOptional.isEmpty()) {
            model.addAttribute("message", """
                Не удалось приобрести билет на заданное место.
                Вероятно оно уже занято.
                Перейдите на страницу бронирования билетов и попробуйте снова.
                """);
            return "errors/404";
        }
        var message = String.format("Ваш сеанс %s, место %s, ряд %s. Приятного просмотра!",
            ticketOptional.get().getSessionId(),
            ticketOptional.get().getPlaceNumber(),
            ticketOptional.get().getRowNumber()
        );
        model.addAttribute("message", message);
        return "tickets/success";
    }
}
