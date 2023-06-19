package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.job4j.cinema.dto.FileDTO;
import ru.job4j.cinema.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;

    private FileController fileController;

    private FileDTO fileDto;


    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        fileDto = mock(FileDTO.class);
    }

    @Test
    void whenRequestFileByIdThenGetOkStatus() {
        when(fileService.getFileById(anyInt())).thenReturn(Optional.of(fileDto));

        var responseEntity = fileController.getById(1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenRequestWrongFileByIdThenGetNotFoundStatus() {
        when(fileService.getFileById(anyInt())).thenReturn(Optional.empty());

        var responseEntity = fileController.getById(1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}