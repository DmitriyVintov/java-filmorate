package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Film create(Film film) {
        if (films.containsKey(film.getId())) {
            throw new DataAlreadyExistException(String.format("Фильм с id %s уже существует", film.getId()));
        }
        film.setId(id);
        films.put(id, film);
        id++;
        return film;
    }

    @Override
    public Film getById(Integer id) {
        return films.get(id);
    }

    @Override
    public List<Film> get() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteById(Integer id) {
        films.remove(id);
    }

    @Override
    public void setLike(Integer filmId, Integer userId) {
        getById(filmId).setLikes(userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        getById(filmId).removeLike(userId);
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        return get().stream()
                .sorted(Comparator.nullsLast(Comparator.comparingInt((Film film) -> film.getLikes().size()))
                        .thenComparing(Film::getReleaseDate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}