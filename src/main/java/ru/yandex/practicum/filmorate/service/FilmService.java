package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.model.UserEventOperation;
import ru.yandex.practicum.filmorate.model.UserEventType;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbUserEventStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Qualifier("dbFilmStorage")
@Slf4j
public class FilmService {
    @Qualifier("dbFilmStorage")
    private final FilmStorage filmStorage;
    @Qualifier("dbUserStorage")
    private final UserStorage userStorage;
    private final DbUserEventStorage eventStorage;
    private static final int MIN_SIZE_LIST_CONDITIONS = 2;

    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage,
                       @Qualifier("dbUserStorage") UserStorage userStorage, DbUserEventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public Film createFilm(Film film) {
        checkFilmOnDuplicate(film);
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
        eventStorage.create(new UserEvent(userId, UserEventType.LIKE, UserEventOperation.ADD, filmId));
    }

    public void removeLike(Integer filmId, Integer userId) {
        validateFilm(filmId);
        validateUser(userId);
        log.info("Пользователь id {} удалил лайк у фильма id {}", userId, filmId);
        eventStorage.create(new UserEvent(userId, UserEventType.LIKE, UserEventOperation.REMOVE, filmId));
        filmStorage.deleteLike(filmId, userId);
    }

    private void validateFilm(Integer id) {

        if (filmStorage.getById(id) == null) {
            log.error("Фильма с id {} не существует", id);
            throw new NotFoundException(String.format("Фильма с id %s не существует", id));
        }
    }

    private void checkFilmOnDuplicate(Film film) {
        List<Film> collect = getFilms().stream().filter(film1 -> film1.getName().equals(film.getName())
                && film1.getReleaseDate().equals(film.getReleaseDate()) && Objects.equals(film.getDuration(), film1.getDuration())).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            throw new DataAlreadyExistException("Такой фильм уже есть");
        }
    }

    private void validateUser(Integer userId) {
        if (userStorage.getById(userId) == null) {
            log.error("Пользователя с id {} не существует", userId);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", userId));
        }
    }

    public List<Film> getSortedFilmsByDirector(int directorId, String sortBy) {
        List<Film> films = filmStorage.getAll();
        List<Film> filmsByDirector = films.stream().filter(film -> film.getDirectors().stream().anyMatch(director -> director.getId() == directorId)).collect(Collectors.toList());
        if (filmsByDirector.isEmpty()) {
            throw new NotFoundException("Фильмов с таким режиссером нет");
        }
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

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        if (count <= 0) {
            throw new ValidationException("Значение count не может быть отрицательным");
        }

        List<Film> films = filmStorage.getAll();

        return films.stream()
                .filter(film -> (genreId == null || filmHasGenre(film, genreId)) &&
                        (year == null || filmReleasedInYear(film, year)))
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());

    }

    private boolean filmHasGenre(Film film, Integer genreId) {
        return film.getGenres().stream().anyMatch(genre -> Objects.equals(genre.getId(), genreId));
    }

    private boolean filmReleasedInYear(Film film, Integer year) {
        return film.getReleaseDate().getYear() == year;
    }


    public List<Film> getFilmsByNameOrDirectorName(String query, String by) {
        List<Film> result = new ArrayList<>();
        List<Film> films = filmStorage.getAll();
        String[] parts = by.split(",");
        if (parts.length == MIN_SIZE_LIST_CONDITIONS) {
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
        return allFilm.stream().filter(e -> e.getLikes().contains(userId))
                .filter(e -> e.getLikes().contains(friendId))
                .sorted((e,e1) -> Integer.compare(e1.getLikes().size(),e.getLikes().size()))
                .collect(Collectors.toList());
    }
}