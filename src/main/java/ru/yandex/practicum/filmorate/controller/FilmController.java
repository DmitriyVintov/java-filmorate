package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(id);
        log.info("Фильм с id {} создан", id);
        films.put(id, film);
        id++;
        return film;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Такого фильма не существует");
            throw new ValidationException("Такого фильма не существует");
        }
        log.info("Фильм с id {} обновлен", id);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping()
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}