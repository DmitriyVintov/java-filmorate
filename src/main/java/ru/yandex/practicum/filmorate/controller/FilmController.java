package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@AllArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на создание фильма.");
        return filmService.createFilm(film);
    }

    @GetMapping()
    public Collection<Film> getFilms() {
        log.info("Поступил запрос на получение всех фильмов");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info(String.format("Поступил запрос на получение фильма с id %s", id));
        return filmService.getFilmById(id);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info(String.format("Поступил запрос на обновление фильма с id %s", film.getId()));
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable Integer id) {
        log.info(String.format("Поступил запрос на удаление фильма с id %s", id));
        filmService.deleteFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Поступил запрос на добавление лайка фильму с id %s от пользователя с id %s", id, userId));
        filmService.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Поступил запрос на удаление лайка фильму с id %s от пользователя с id %s", id, userId));
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> topFilms(@RequestParam(required = false, defaultValue = "10") Integer count,
                               @RequestParam(required = false) Integer genreId,
                               @RequestParam(required = false) Integer year) {
        log.debug("Поступил запрос на просмотр топ {} фильмов.", count);
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getSortedFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getFilmsByNameOrDirectorName(@RequestParam String query, @RequestParam String by) {
        return filmService.getFilmsByNameOrDirectorName(query, by);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilm(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}