package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends Storage<Film> {

    List<Film> getAll();

    Film getById(Integer id) throws NotFoundException;

    Film create(Film user);

    Film update(Film user);

    void deleteById(Integer id);

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);
}