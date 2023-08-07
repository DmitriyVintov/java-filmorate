package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@AllArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        Optional<Film> first = filmService.getFilms().stream()
                .filter(film1 -> film1.getName().equals(film.getName()))
                .findFirst();
        if (first.isPresent()) {
            throw new ValidationException("Фильм с таким названием уже существует");
        }
        return filmService.createFilm(film);
    }

    @GetMapping()
    public Collection<Film> getFilms() {
        List<Film> films = new ArrayList<>();
        try {
            films = filmService.getFilms();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        if (!filmService.getFilms().contains(filmService.getFilmById(id))) {
            log.error("Фильма с id {} не существует", id);
            throw new NotFoundException(String.format("Фильма с id %s не существует", id));
        }
        return filmService.getFilmById(id);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        Optional<Film> first = filmService.getFilms().stream()
                .filter(film1 -> film1.getId().equals(film.getId()))
                .findFirst();
        if (first.isEmpty()) {
            log.error("Фильма с id {} не существует", film.getId());
            throw new NotFoundException(String.format("Фильма с id %s не существует", film.getId()));
        }
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable Integer id) {
        if (!filmService.getFilms().contains(filmService.getFilmById(id))) {
            log.error("Фильма с id {} не существует", id);
            throw new NotFoundException(String.format("Фильма с id %s не существует", id));
        }
        filmService.deleteFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable Integer id, @PathVariable Integer userId) {
        if (!filmService.getFilms().contains(filmService.getFilmById(id))) {
            log.error("Фильма с id {} не существует", id);
            throw new NotFoundException(String.format("Фильма с id %s не существует", id));
        }
        if (!userService.getUsers().contains(userService.getById(userId))) {
            log.error("Пользователя с id {} не существует", userId);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", userId));
        }
        filmService.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        if (id < 0 || userId < 0) {
            log.error("Фильма с id {} или пользователя с {} не существует", id, userId);
            throw new NotFoundException(String.format("Фильма с id %s или пользователя с %s не существует", id, userId));
        }
        if (!filmService.getFilms().contains(filmService.getFilmById(id))) {
            log.error("Фильма с id {} не существует", id);
            throw new NotFoundException(String.format("Фильма с id %s не существует", id));
        }
        if (!userService.getUsers().contains(userService.getById(userId))) {
            log.error("Пользователя с id {} не существует", userId);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", userId));
        }
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        if (count <= 0) {
            throw new ValidationException("Значение count не может быть отрицательным");
        }
        return filmService.getMostPopularFilms(count);
    }
}