package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("Поступил запрос на получение всех режиссеров");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable int id) {
        log.info(String.format("Поступил запрос на получение режиссера с id %s", id));
        return directorService.getById(id);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Поступил запрос на создание режиссера");
        return directorService.create(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info(String.format("Поступил запрос на обновление режиссера с id %s", director.getId()));
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        log.info(String.format("Поступил запрос на удаление режиссера с id %s", id));
        directorService.deleteById(id);
    }
}
