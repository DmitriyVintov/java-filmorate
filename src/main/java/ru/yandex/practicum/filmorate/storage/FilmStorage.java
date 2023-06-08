package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends Storage<Film> {
    Film create(Film film);

    List<Film> get();

    Film update(Film film);

    Film getById(Integer id);

    void deleteById(Integer id);
}