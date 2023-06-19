package ru.job4j.cinema.dto;

import java.time.LocalDateTime;

public class FilmSessionDTO {

    private int id;
    private String filmName;
    private String hallName;
    private String hallDescription;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public FilmSessionDTO(int id, String filmName, String hallName, String hallDescription, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.filmName = filmName;
        this.hallName = hallName;
        this.hallDescription = hallDescription;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public String getHallDescription() {
        return hallDescription;
    }

    public void setHallDescription(String hallDescription) {
        this.hallDescription = hallDescription;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
