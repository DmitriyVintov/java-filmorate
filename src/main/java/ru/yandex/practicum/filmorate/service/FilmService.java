package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("dbFilmStorage")
@Slf4j
public class FilmService {
    @Qualifier("dbFilmStorage")
    private final FilmStorage filmStorage;
    @Qualifier("dbUserStorage")
    private final UserStorage userStorage;

    private final Storage<Director> directorStorage;

    private static final int MIN_SIZE = 2;

    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage,
                       @Qualifier("dbUserStorage") UserStorage userStorage,
                       Storage<Director> directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
    }

    public Film createFilm(Film film) {
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
        log.info("Фильм обновлен: {}", film);
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
        directorStorage.getById(directorId);
        List<Film> films = filmStorage.getAll();
        List<Film> filmsByDirector = films.stream().filter(film -> film.getDirectors().stream().anyMatch(director -> director.getId() == directorId)).collect(Collectors.toList());
        switch (sortBy) {
            case "year":
                filmsByDirector = filmsByDirector.stream().sorted(Comparator.comparing(Film::getReleaseDate)).collect(Collectors.toList());
                break;
            case "likes":
                filmsByDirector = filmsByDirector.stream().sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size()).collect(Collectors.toList());
                break;
            default:
                throw new javax.validation.ValidationException("Неверные параметры в запросе!");
        }
        return filmsByDirector;
    }

    public List<Film> getFilmsByNameOrDirectorName(String query, String by) {
        List<Film> result = new ArrayList<>();
        List<Film> films = filmStorage.getAll();
        String[] parts = by.split(",");
        if (parts.length == MIN_SIZE) {
            result.addAll(films.stream()
                    .filter(film -> film.getDirectors().stream().anyMatch(director -> director.getName()
                            .contains(query.toLowerCase()))).collect(Collectors.toList()));
            result.addAll(films.stream()
                    .filter(film -> film.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList()));
        } else {
            if (parts[0].equals("director")) {
                result = films.stream()
                        .filter(film -> film.getDirectors().stream()
                                .anyMatch(director -> director.getName().contains(query.toLowerCase())))
                        .collect(Collectors.toList());
            } else if (parts[0].equals("title")) {
                result = films.stream()
                        .filter(film -> film.getName().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return result.stream().sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .collect(Collectors.toList());
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        List<Film> allFilm = getFilms();
        List<Film> commonFilmsList = allFilm.stream().filter(e -> e.getLikes().contains(userId))
                .filter(e -> e.getLikes().contains(friendId))
                .sorted((e,e1) -> Integer.compare(e1.getLikes().size(),e.getLikes().size()))
                .collect(Collectors.toList());
        return commonFilmsList;
    }
}