package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("dbFilmStorage")
@Slf4j
public class FilmService {
    @Qualifier("dbFilmStorage")
    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        log.info("Создан фильм: {}", film.toString());
        return filmStorage.create(film);
    }

    public List<Film> getFilms() {
        log.info("Все фильмы: {}", filmStorage.getAll());
        return filmStorage.getAll();
    }

    public Film getFilmById(Integer id) {
        log.info("Получение фильма с id {}: {}", id, filmStorage.getById(id));
        return filmStorage.getById(id);
    }

    public Film updateFilm(Film film) {
        log.info("Фильм обновлен: {}", film.toString());
        return filmStorage.update(film);
    }

    public void deleteFilmById(Integer id) {
        log.info("Фильм с id {} удален", id);
        filmStorage.deleteById(id);
    }

    public void setLike(Integer filmId, Integer userId) {
        log.info("Пользователь id {} поставил лайк фильму id {}", userId, filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        log.info("Пользователь id {} удалил лайк у фильма id {}", userId, filmId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        log.info("Получение списка самых популярных фильмов: {}", filmStorage.getAll().stream()
                .sorted(Comparator.nullsLast(Comparator.comparingInt((Film film) -> film.getLikes().size()))
                        .thenComparing(Film::getReleaseDate).reversed())
                .limit(count)
                .collect(Collectors.toList()));
        return filmStorage.getAll().stream()
                .sorted(Comparator.nullsLast(Comparator.comparingInt((Film film) -> film.getLikes().size()))
                        .thenComparing(Film::getReleaseDate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}