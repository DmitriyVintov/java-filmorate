package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends StorageAdv<Film> {
    Film create(Film film);

    List<Film> get();

    Film update(Film film);

    Film getById(Integer id);

    void deleteById(Integer id);

    void setLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    List<Film> getMostPopularFilms(Integer count);
}