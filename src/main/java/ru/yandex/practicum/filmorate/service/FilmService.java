package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Qualifier("dbFilmStorage")
@Slf4j
public class FilmService {
    @Qualifier("dbFilmStorage")
    private final FilmStorage filmStorage;
    @Qualifier("dbUserStorage")
    private final UserStorage userStorage;

    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage, @Qualifier("dbUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        Optional<Film> first = getFilms().stream()
                .filter(film1 -> film1.getName().equals(film.getName()))
                .findFirst();
        if (first.isPresent()) {
            throw new DataAlreadyExistException("Фильм с таким названием уже существует");
        }
        log.info("Создан фильм: {}", film.toString());
        return filmStorage.create(film);
    }

    public List<Film> getFilms() {
        List<Film> allFilms = filmStorage.getAll();
        log.info("Все фильмы: {}", allFilms);
        return allFilms;
    }

    public Film getFilmById(Integer id) {
        validateFilm(id);
        Film filmById = filmStorage.getById(id);
        log.info("Получение фильма с id {}: {}", id, filmById);
        return filmById;
    }

    public Film updateFilm(Film film) {
        validateFilm(film.getId());
        log.info("Фильм обновлен: {}", film.toString());
        return filmStorage.update(film);
    }

    public void deleteFilmById(Integer id) {
        validateFilm(id);
        log.info("Фильм с id {} удален", id);
        filmStorage.deleteById(id);
    }

    public void setLike(Integer filmId, Integer userId) {
        validateFilm(filmId);
        validateUser(userId);
        log.info("Пользователь id {} поставил лайк фильму id {}", userId, filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        validateFilm(filmId);
        validateUser(userId);
        log.info("Пользователь id {} удалил лайк у фильма id {}", userId, filmId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        if (count <= 0) {
            throw new ValidationException("Значение count не может быть отрицательным");
        }
        List<Film> mostPopularFilms = filmStorage.getAll().stream()
                .sorted(Comparator.nullsLast(Comparator.comparingInt((Film film) -> film.getLikes().size()))
                        .thenComparing(Film::getReleaseDate).reversed())
                .limit(count)
                .collect(Collectors.toList());
        log.info("Получение списка самых популярных фильмов: {}", mostPopularFilms);
        return mostPopularFilms;
    }

    private void validateFilm(Integer id) {
        if (filmStorage.getById(id) == null) {
            log.error("Фильма с id {} не существует", id);
            throw new NotFoundException(String.format("Фильма с id %s не существует", id));
        }
    }

    private void validateUser(Integer userId) {
        if (userStorage.getById(userId) == null) {
            log.error("Пользователя с id {} не существует", userId);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", userId));
        }
    }

    public List<Film> getSortedFilmsByDirector(int directorId, String sortBy) {
        return filmStorage.getSortedFilmsByDirector(directorId, sortBy);
    }
}