package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final Storage<Genre> genreStorage;

    public GenreService(Storage<Genre> genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getGenres() {
        List<Genre> allGenres = genreStorage.getAll();
        log.info("Все жанры: {}", allGenres);
        return allGenres;
    }

    public Genre getGenreById(Integer id) {
        Genre genreById = genreStorage.getById(id);
        if (genreById == null) {
            log.error("Жанра с id {} не существует", id);
            throw new NotFoundException(String.format("Жанра с id %s не существует", id));
        }
        log.info("Получение жанра с id {}: {}", id, genreById);
        return genreById;
    }
}