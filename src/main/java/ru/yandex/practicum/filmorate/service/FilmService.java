package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

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
        Film newFilm = filmStorage.create(film);
        log.info("Создан фильм: {}", film.toString());
        return newFilm;
    }

    public List<Film> getFilms() {
        log.info("Получение всех фильмов");
        return filmStorage.get();
    }

    public Film getFilmById(Integer id) {
        log.info("Получение фильма с id {}", id);
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
        filmStorage.setLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        log.info("Пользователь id {} удалил лайк у фильма id {}", userId, filmId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        log.info("Получение списка самых популярных фильмов");
        return filmStorage.getMostPopularFilms(count);
    }
}