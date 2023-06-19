package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;

@ThreadSafe
@Controller
@RequestMapping("/filmSessions")
public class FilmSessionController {

    private final FilmSessionService filmSessionService;

    private final HallService hallService;

    private final FilmService filmService;

    public FilmSessionController(FilmSessionService filmSessionService, HallService hallService, FilmService filmService) {
        this.filmSessionService = filmSessionService;
        this.hallService = hallService;
        this.filmService = filmService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("filmSessions", filmSessionService.findAll());
        return "filmSessions/list";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var filmSessionOptional = filmSessionService.findById(id);
        if (filmSessionOptional.isEmpty()) {
            model.addAttribute("message", "Сеанс с указанным идентификатором не найден");
            return "errors/404";
        }
        model.addAttribute("filmSession", filmSessionOptional.get());
        model.addAttribute("rows", hallService.getRowsByHallId(filmSessionOptional.get().getHallId()));
        model.addAttribute("places", hallService.getPlacesByHallId(filmSessionOptional.get().getHallId()));
        model.addAttribute("film", filmService.findById(filmSessionOptional.get().getFilmId()).get());
        return "tickets/buy";
    }
}
