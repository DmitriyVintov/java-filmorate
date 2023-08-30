package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.ValidationException;
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
    public List<Film> getAll() {
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
    public void addLike(Integer filmId, Integer userId) {
        getById(filmId).setLike(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        getById(filmId).removeLike(userId);
    }

    @Override
    public List<Film> getSortedFilmsByDirector(int directorId, String sortBy) {
        List<Film> films = getAll();
        List<Film> filmsByDirector = films.stream().filter(film -> film.getDirectors().stream().anyMatch(director -> director.getId() == directorId)).collect(Collectors.toList());
        switch (sortBy) {
            case "year":
                filmsByDirector = filmsByDirector.stream().sorted(Comparator.comparing(Film::getReleaseDate)).collect(Collectors.toList());
                break;
            case "likes":
                filmsByDirector = filmsByDirector.stream().sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size()).collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Неверные параметры в запросе!");
        }
        return filmsByDirector;
    }
}